package com.sparta.meeting_platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.domain.UserRoleEnum;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.KakaoDto.KakaoUserInfoDto;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialKakaoService {

    @Value("${kakao.client_id}")
    String kakaoClientId;
    @Value("${kakao.redirect_uri}")
    String RedirectURI;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleCheckService userRoleCheckService;
    private final UserService userService;

    @Transactional
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(String code)
            throws JsonProcessingException {
        // 1. "인가코드" 로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 카카오ID로 회원가입 처리
        User kakaoUser = signupKakaoUser(kakaoUserInfo);

        //4. 강제 로그인 처리
        Authentication authentication = forceLoginKakaoUser(kakaoUser);

        // User 권한 확인
        userRoleCheckService.userRoleCheck(kakaoUser);

        //  5. response Header에 JWT 토큰 추가
        userService.accessAndRefreshTokenProcess(kakaoUser.getUsername());

        String nickname = kakaoUser.getNickName();
        int mannerTemp = kakaoUser.getMannerTemp();

        return new ResponseEntity<>(new FinalResponseDto<>
                (true, "로그인 성공", nickname, mannerTemp, kakaoUser.getId()), HttpStatus.OK);
    }

    //header 에 Content-type 지정
    //1번
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", RedirectURI);
        body.add("code", code);

        //HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        //HTTP 응답 (JSON) -> 액세스 토큰 파싱
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        log.info("인가코드로 액세스 토큰 요청 {}", jsonNode.get("access_token").asText());
        return jsonNode.get("access_token").asText();
    }

    //2번
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        //HTTP 응답 (JSON)
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String profileUrl = jsonNode.get("properties")
                .get("profile_image").asText();
        log.info("카카오 사용자 정보 id: {},{},{},{}", id, nickname, email, profileUrl);
        return new KakaoUserInfoDto(id, nickname, email, profileUrl);
    }

    // 3번
    private User signupKakaoUser(KakaoUserInfoDto kakaoUserInfoDto) {
        // 재가입 방지
        int mannerTemp = userRoleCheckService.userResignCheck(kakaoUserInfoDto.getEmail());
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfoDto.getKakaoId();
        User findKakao = userRepository.findByUsername(kakaoUserInfoDto.getEmail())
                .orElse(null);

        //DB에 중복된 계정이 없으면 회원가입 처리
        if (findKakao == null) {
            String nickName = kakaoUserInfoDto.getNickname();
            String profileUrl = kakaoUserInfoDto.getProfileUrl();
            String email = kakaoUserInfoDto.getEmail();
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            LocalDateTime createdAt = LocalDateTime.now();
            User kakaoUser = User.builder()
                    .username(email)
                    .nickName(nickName)
                    .password(encodedPassword)
                    .profileUrl(profileUrl)
                    .createdAt(createdAt)
                    .kakaoId(kakaoId)
                    .mannerTemp(mannerTemp)
                    .isOwner(false)
                    .role(UserRoleEnum.USER)
                    .build();
            userRepository.save(kakaoUser);
            log.info("카카오 아이디로 회원가입 {}", kakaoUser);

            return kakaoUser;
        }
        log.info("카카오 아이디가 있는 경우 {}", findKakao);
        return findKakao;
    }

    // 4번
    public Authentication forceLoginKakaoUser(User kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("강제 로그인 {}", authentication);
        return authentication;
    }
}
