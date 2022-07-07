package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.user.DuplicateRequestDto;
import com.sparta.meeting_platform.dto.user.LoginRequestDto;
import com.sparta.meeting_platform.dto.user.ProfileRequestDto;
import com.sparta.meeting_platform.dto.user.SignupRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //유저네임 중복 체크
    @PostMapping("/user/duplicate/username")
    public ResponseEntity<FinalResponseDto<?>> duplicateUsername (@RequestBody DuplicateRequestDto requestDto){
        return userService.duplicateUsername(requestDto);
    }

    //회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<FinalResponseDto<?>> signup (@RequestBody SignupRequestDto requestDto) throws MessagingException {
        return userService.signup(requestDto);
    }

    //로그인
    @PostMapping("/user/login")
    public ResponseEntity<FinalResponseDto<?>> login (@RequestBody LoginRequestDto requestDto) {
        return userService.login(requestDto);
    }

    //프로필 설정
    @PostMapping("/user/profile")
    public ResponseEntity<FinalResponseDto<?>> setProfile (
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(value = "profileDto") ProfileRequestDto requestDto,
            @RequestPart(value = "profileImg") MultipartFile file) {
        return userService.setProfile(userDetails.getUser().getId(), requestDto, file);
    }

    // 회원 탈퇴
   @DeleteMapping("/user")
    public ResponseEntity<FinalResponseDto<?>> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(userDetails.getUser().getId());
    }

}
