package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final UserService userService;

    @GetMapping("/confirmEmail")
    public String viewConfirmEmail(@Valid @RequestParam String token){
        userService.confirmEmail(token);
        return "redirect:https://www.naver.com";
    }

}
