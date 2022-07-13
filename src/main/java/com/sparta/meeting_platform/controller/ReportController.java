package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.ReportDto.ReportRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/user/report/{userId}")
    public ResponseEntity<FinalResponseDto<?>> setUserReport (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long userId,
                                                              @RequestBody ReportRequestDto reportRequestDto) {
        return reportService.setUserReport(userDetails.getUser().getId(), userId, reportRequestDto.getHistory());
    }

    @GetMapping("/user/reports")
    public ResponseEntity<FinalResponseDto<?>> getUserReport (@AuthenticationPrincipal UserDetailsImpl userDetails){
        return reportService.getUserReport(userDetails.getUser().getId());
    }


}