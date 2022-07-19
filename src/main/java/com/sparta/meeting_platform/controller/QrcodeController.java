package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.QrcodeCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class QrcodeController {

    private final QrcodeCheckService qrcodeCheckService;

    @GetMapping("/uesr/qrcode")
    public String qrcodeCheck (@RequestParam Long postId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        qrcodeCheckService.qrcodeUserCheck(postId,userDetails.getUser().getId());
        return "redirect:http://localhost:3000/main";
    }
}
