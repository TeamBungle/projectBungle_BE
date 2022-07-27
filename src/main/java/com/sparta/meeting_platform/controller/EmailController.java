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
        return "redirect:https://bungle.life/";
    }




}
