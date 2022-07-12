package com.sparta.meeting_platform.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sparta.meeting_platform.config.GoogleConfig;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.domain.UserRoleEnum;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginRequestDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginResponseDto;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialGoogleService {

    private final UserRepository userRepository;
    private final GoogleConfig googleConfig;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final  UserRoleCheckService userRoleCheckService;

    public ResponseEntity<FinalResponseDto<?>> googleLogin
            (String authCode, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        // 1. "인가코드" 로 "액세스 토큰" 요청
        // 2. 토큰으로 구글 API 호출
        // 3. 구글ID로 회원가입 처리
        // 4. 강제 로그인 처리
        // 5. response Header에 JWT 토큰 추가


        // Gooogle Oauth Access Token 요청용 Dto
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .clientId(googleConfig.getGoogleClientId())     // API Console Credentials page 에서 가져온 클라이언트 ID
                .clientSecret(googleConfig.getGoogleSecret())       // Credentials page 에서 가져온 API Console 클라이언트 보안 비밀번호
                .code(authCode)     // 초기 요청에서 반환된 승인 코드
                .redirectUri(googleConfig.getGoogleRedirectUri())       // 지정된 client_id의 API ConsoleCredentials page 에서 프로젝트에 나열된 리디렉션 URI중 하나
                .grantType("authorization_code")        // OAuth 2.0 사양에 정의된 대로 이 필드 값을 authorization_code로 설정
                .build();

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequestDto> httpRequestEntity = new HttpEntity<>(googleLoginRequestDto, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity
                (googleConfig.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        // ObjectMapper를 통해 String to Object로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
        GoogleLoginResponseDto googleLoginResponseDto
                = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponseDto>() {
        });

        // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
        String jwtToken = googleLoginResponseDto.getIdToken();

        //======================= 1단계(액세스 토큰 요청) 끝 =======================

        // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
        String requestUrl = UriComponentsBuilder.fromHttpUrl
                (googleConfig.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        GoogleLoginDto userInfoDto = new GoogleLoginDto();
        if (resultJson != null) {
            userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {
            });
        }

        // 3번 회원가입
        // 재가입 방지
        int mannerTemp = userRoleCheckService.userResignCheck(userInfoDto.getEmail());
        User user = userRepository.findByUsername(userInfoDto.getEmail()).orElse(null);

        if (user == null) {
            String username = userInfoDto.getEmail(); // username: google ID(email)
            String nickName = userInfoDto.getName();
            String password = UUID.randomUUID().toString(); // password: random UUID
            String encodedPassword = passwordEncoder.encode(password);
            String profileImage = userInfoDto.getPicture(); // profileImage: google profile image
            LocalDateTime createdAt = LocalDateTime.now();
            String googleId = userInfoDto.getSub(); // 구글 고유키
            user = User.builder()
                    .username(username)
                    .nickName(nickName)
                    .password(encodedPassword)
                    .profileUrl(profileImage)
                    .createdAt(createdAt)
                    .googleId(googleId)
                    .mannerTemp(mannerTemp)
                    .isOwner(false)
                    .role(UserRoleEnum.USER)
                    .build();
            userRepository.save(user);
        }

        // 4번 강제 로그인 처리
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // User 권한 확인
        userRoleCheckService.userRoleCheck(user);

        // 5번 response Header에 JWT 토큰 추가
        String jwt_token = jwtTokenProvider.generateJwtToken(userDetails);
        headers.set("Authorization", "BEARER" + " " + jwt_token);
        httpServletResponse.addHeader("Authorization", "Bearer" + " " + jwt_token);

        return new ResponseEntity<>(new FinalResponseDto<>
                (true, "로그인 성공!!", user.getNickName(), user.getMannerTemp(),user.getUsername() ), HttpStatus.OK);
    }

}
