package com.sparta.meeting_platform.chat.controller;

import com.sparta.meeting_platform.chat.dto.NotificationDto;
import com.sparta.meeting_platform.chat.service.NotificationService;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/noti")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return notificationService.getNotification(userDetails);
    }
}
