package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.exception.EmailApiException;
import com.sparta.meeting_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final UserService userService;

    @GetMapping("/user/confirmEmail")
    public String viewConfirmEmail(@Valid @RequestParam String token){
        userService.confirmEmail(token);
        return "redirect:http://localhost:3000/";
    }

    @GetMapping("/user/confirmEmail2")
    public String viewConfirmEmail2(@Valid @RequestParam String token){
        System.out.println("컨트롤 왔냐");
        String msg = userService.confirmEmail(token);
        if(msg.equals("기존 인증 코드가 만료되어 이메일 재발송 하였습니다.")){
            throw new EmailApiException(msg);
        }
        return "redirect:https://www.naver.com";
    }

}
