package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.Google.GoogleResponseDto;
import com.sparta.meeting_platform.service.SocialGoogleService;

import com.sparta.meeting_platform.service.SocialKakaoService;
import com.sparta.meeting_platform.service.SocialNaverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user/signin")
@Slf4j
public class SocialController {

    private final SocialGoogleService socailService;
    private final SocialNaverService socialNaverService;
    public final SocialKakaoService socialKakaoService;

    public SocialController(SocialGoogleService socailService, SocialKakaoService socialKakaoService,SocialNaverService socialNaverService){
        this.socailService = socailService;
        this.socialKakaoService = socialKakaoService;
        this.socialNaverService = socialNaverService;

    }

    //https://accounts.google.com/o/oauth2/v2/auth?client_id=1063605264794-i2qeg4lqsmi3u60pu51cdqcm9eemrb23.apps.googleusercontent.com&redirect_uri=http://localhost:8080/user/google/signin&response_type=code&scope=email%20profile%20openid&access_type=offline
    @GetMapping("/google")
    public GoogleResponseDto googleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        GoogleResponseDto googleUserResponseDto = socailService.googleLogin(authCode, httpServletResponse);
        return googleUserResponseDto;
    }
    @GetMapping("/kakao")
    public User kakaoLogin(@RequestParam(value = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("요청 메서드 [GET] /api/user/kakao/callback");
        return socialKakaoService.kakaoLogin(code, response);
    }

    @ResponseBody
    @GetMapping("/naver")
    public String naverLogin(@RequestParam String code, @RequestParam String state)  {
        return socialNaverService.getNaverAccessToken(code, state);
    }

}
