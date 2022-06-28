package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.dto.Google.GoogleResponseDto;
import com.sparta.meeting_platform.service.SocialGoogleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class SocialController {

    private final SocialGoogleService socailService;

    public SocialController(SocialGoogleService socailService){
        this.socailService = socailService;
    }

    //https://accounts.google.com/o/oauth2/v2/auth?client_id=1063605264794-i2qeg4lqsmi3u60pu51cdqcm9eemrb23.apps.googleusercontent.com&redirect_uri=http://localhost:8080/user/google/signin&response_type=code&scope=email%20profile%20openid&access_type=offline
    @GetMapping("/user/signin/google")
    public GoogleResponseDto googleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        GoogleResponseDto googleUserResponseDto = socailService.googleLogin(authCode, httpServletResponse);
        return googleUserResponseDto;
    }

}
