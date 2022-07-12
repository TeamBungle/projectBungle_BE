package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/user/report/{userId}")
    public ResponseEntity<FinalResponseDto<?>> setUserReport (@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        // 신고 내용 추가
        // 날짜 , 시간, 신고 당한 유저 프사
        return reportService.setUserReport(userDetails.getUser().getId(), userId);
    }

    @GetMapping("/user/reports")
    public ResponseEntity<FinalResponseDto<?>> getUserReport (@AuthenticationPrincipal UserDetailsImpl userDetails){
        // 신고 내용 반환
        return reportService.getUserReport(userDetails.getUser().getId());
    }


}