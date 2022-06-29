package com.sparta.meeting_platform.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sparta.meeting_platform.config.GoogleConfig;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.Google.GoogleLoginDto;
import com.sparta.meeting_platform.dto.Google.GoogleLoginRequestDto;
import com.sparta.meeting_platform.dto.Google.GoogleLoginResponseDto;
import com.sparta.meeting_platform.dto.Google.GoogleResponseDto;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class SocialGoogleService {

    private final UserRepository userRepository;
    private final GoogleConfig googleConfig;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SocialGoogleService(UserRepository userRepository, GoogleConfig googleConfig, PasswordEncoder passwordEncoder,JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.googleConfig = googleConfig;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public GoogleResponseDto googleLogin(String authCode, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        // Gooogle Oauth Access Token 요청용 Dto
        GoogleLoginRequestDto googleLoginRequestDto = GoogleLoginRequestDto.builder()
                .clientId(googleConfig.getGoogleClientId())
                .clientSecret(googleConfig.getGoogleSecret())
                .code(authCode)
                .redirectUri(googleConfig.getGoogleRedirectUri())
                .grantType("authorization_code")
                .build();

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequestDto> httpRequestEntity = new HttpEntity<>(googleLoginRequestDto, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfig.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        // ObjectMapper를 통해 String to Object로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
        GoogleLoginResponseDto googleLoginResponseDto = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponseDto>() {
        });

        // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
        String jwtToken = googleLoginResponseDto.getIdToken();

        //======================= 1단계(액세스 토큰 요청) 끝 =======================

        // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleConfig.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        GoogleLoginDto userInfoDto = new GoogleLoginDto();
        if (resultJson != null) {
            userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {
            });
        }

        //======================= 2단계(유저 정보 가져오기) 끝 =======================

        User user = userRepository.findByUsername(userInfoDto.getEmail()).orElse(null);

        if (user == null) {

            // 회원가입
            // username: google ID(email)
            String username = userInfoDto.getEmail();

            // profileImage: google profile image
            String profileImage = userInfoDto.getPicture();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            String nickName = userInfoDto.getName();

            user = User.builder()
                    .username(username)
                    .password(encodedPassword)
                    .profileUrl(profileImage)
                    .nickName(nickName)
                    .build();
            userRepository.save(user);
        } else {
            // 소셜 서버의 내용이 변경 되었을때 서비스 서버도 해당 내용 변경되어야 하는가?
        }

        //======================= 3단계(회원 가입) 끝 =======================


//        강제 로그인 처리
        final String AUTH_HEADER = "Authorization";
        final String TOKEN_TYPE = "BEARER";

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //*** jwtTokenProvider클래스 내에 generateJwtToken함수로 토큰을 제작했습니다. 확인해 주세요!!
        String jwt_token = jwtTokenProvider.generateJwtToken(userDetails);
        headers.set(AUTH_HEADER, TOKEN_TYPE + " " + jwt_token);

        GoogleResponseDto googleResponseDto = GoogleResponseDto.builder()
                .success(true)
                .message("로그인 성공")
                .nickname(user.getNickName())
                .mannerTemp(0.0F)
                .build();

        httpServletResponse.addHeader("Authorization", TOKEN_TYPE + " " + jwt_token);

        return googleResponseDto;
    }

}
