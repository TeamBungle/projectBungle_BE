package com.sparta.meeting_platform.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.Naver.NaverUserDto;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SocialNaverService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인 기능 (추후 정훈님 완료되면 리턴값 변경하여 ResponseDto 리턴 예정)
    public ResponseEntity<FinalResponseDto<?>> naverLogin(String code, String state, HttpServletResponse response) {

        try {
            // 네이버에서 가져온 유저정보 + 임의 비밀번호 생성
            NaverUserDto naverUser = getNaverUserInfo(code, state);
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            // 네이버 ID로 유저 정보 DB 에서 조회
            User user = userRepository.findByNaverId(naverUser.getNaverId()).orElse(null);

            // 없으면 회원가입
            if (user == null) {
                user = User.builder()
                        .username(naverUser.getEmail())
                        .password(encodedPassword)
                        .nickName(naverUser.getNickName())
                        .profileUrl(naverUser.getProfileUrl())
                        .naverId(naverUser.getNaverId())
                        .mannerTemp(36.5F)
                        .build();
                userRepository.save(user);
            }
            // 강제 로그인
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null);
            UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
            String token = jwtTokenProvider.generateJwtToken(userDetailsImpl);
            response.addHeader("Authorization", "BEARER" + " " + token);
            return new ResponseEntity<>(new FinalResponseDto<>
                    (true, "로그인 성공!!", user.getNickName(), user.getMannerTemp()), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new FinalResponseDto<>
                    (false, "로그인 실패"), HttpStatus.BAD_REQUEST);
        }
    }


    // 네이버에 요청해서 데이터 전달 받는 메소드
    public JsonElement jsonElement(String reqURL, String token, String code, String state) throws IOException {

        // 요청하는 URL 설정
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // POST 요청에 필요한 데이터 저장 후 전송
        if (token == null) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +  // TODO grant_type 입력
                    "&client_id=fNINb0JLOoWHKPO8p2HO" + // TODO client-id 입력
                    "&client_secret=5PzjffB3yr" + // TODO client_secret 입력
                    "&redirect_uri=http://localhost:3000/oauth/callback/naver" + // TODO 인가코드 받은 redirect_uri 입력
                    "&code=" + code +
                    "&state=" + state;
            bw.write(sb);
            bw.flush();
            bw.close();
        } else {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }

        // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();

        // Gson 라이브러리에 포함된 클래스로 JSON 파싱
        return JsonParser.parseString(result.toString());
    }


    // 네이버에 요청해서 회원정보 받는 메소드
    public NaverUserDto getNaverUserInfo(String code, String state) throws IOException {

        String codeReqURL = "https://nid.naver.com/oauth2.0/token";
        String tokenReqURL = "https://openapi.naver.com/v1/nid/me";

        // 코드를 네이버에 전달하여 엑세스 토큰 가져옴
        JsonElement tokenElement = jsonElement(codeReqURL, null, code, state);
        String access_Token = tokenElement.getAsJsonObject().get("access_token").getAsString();

        // 엑세스 토큰을 네이버에 전달하여 유저정보 가져옴
        JsonElement userInfoElement = jsonElement(tokenReqURL, access_Token, null, null);
        String naverId = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("id"));
        String email = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("email"));
        String nickName = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("nickname"));
        String profileUrlValue = String.valueOf(userInfoElement.getAsJsonObject().get("response")
                .getAsJsonObject().get("profile_image"));
        String profileUrl = profileUrlValue.replaceAll("\\\\", "");

        return new NaverUserDto(naverId, email, nickName, profileUrl);
    }

}
