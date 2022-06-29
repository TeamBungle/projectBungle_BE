package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.dto.Google.GoogleResponseDto;
import com.sparta.meeting_platform.service.SocialGoogleService;
import com.sparta.meeting_platform.service.SocialNaverService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user/signin")
public class SocialController {

    private final SocialGoogleService socailService;
    private final SocialNaverService socialNaverService;

    public SocialController(SocialGoogleService socailService, SocialNaverService socialNaverService){
        this.socailService = socailService;
        this.socialNaverService = socialNaverService;
    }

    //https://accounts.google.com/o/oauth2/v2/auth?client_id=1063605264794-i2qeg4lqsmi3u60pu51cdqcm9eemrb23.apps.googleusercontent.com&redirect_uri=http://localhost:8080/user/google/signin&response_type=code&scope=email%20profile%20openid&access_type=offline
    @GetMapping("/google")
    public GoogleResponseDto googleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        GoogleResponseDto googleUserResponseDto = socailService.googleLogin(authCode, httpServletResponse);
        return googleUserResponseDto;
    }

    @ResponseBody
    @GetMapping("/naver")
    // 추후 정훈님 완료되면 리턴값 변경하여 ResponseDto 리턴 예정
    public void naverLogin(@RequestParam String code, @RequestParam String state)  {
        socialNaverService.naverLogin(code, state);
    }
}
