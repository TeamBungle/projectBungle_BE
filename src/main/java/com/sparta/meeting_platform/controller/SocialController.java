package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.service.SocialGoogleService;

import com.sparta.meeting_platform.service.SocialKakaoService;
import com.sparta.meeting_platform.service.SocialNaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/signin")
@Slf4j
public class SocialController {

    private final SocialGoogleService socialGoogleService;
    private final SocialNaverService socialNaverService;
    public final SocialKakaoService socialKakaoService;

    @GetMapping("/google")
    public ResponseEntity<FinalResponseDto<?>> googleLogin(@RequestParam(value = "code") String authCode,
                                         HttpServletResponse httpServletResponse) throws JsonProcessingException {
        return socialGoogleService.googleLogin(authCode, httpServletResponse);
    }


    @GetMapping("/kakao")
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(@RequestParam(value = "code") String code,
                                                          HttpServletResponse response) throws JsonProcessingException {
        log.info("요청 메서드 [GET] /api/user/kakao/callback");
        return socialKakaoService.kakaoLogin(code, response);
    }


    @GetMapping("/naver")
    public String naverLogin(@RequestParam String code, @RequestParam String state)  {
        return socialNaverService.getNaverAccessToken(code, state);
    }

}
