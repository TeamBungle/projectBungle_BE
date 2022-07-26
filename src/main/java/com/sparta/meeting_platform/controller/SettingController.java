package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.NoticeRequestDto;
import com.sparta.meeting_platform.dto.SettingDto.OpinionRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingController {

    private final SettingService SettingService;


    private Long getUserId(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return user.getId();
    }

    //공지사항 조회
    @GetMapping("/notice")
    public ResponseEntity<FinalResponseDto<?>> getNotice(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return SettingService.getNotice(userId);
    }

    //공지사항 작성
    @PostMapping("/notice")
    public ResponseEntity<FinalResponseDto<?>> createNotice(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody NoticeRequestDto requestDto) {
        Long userId = getUserId(userDetails);
        return SettingService.createNotice(userId, requestDto);
    }

    //의견 보내기
    @PostMapping("/opinion")
    public ResponseEntity<FinalResponseDto<?>> createOpinion
    (@RequestBody OpinionRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return SettingService.creatOpinion(requestDto, userId);
    }
}
