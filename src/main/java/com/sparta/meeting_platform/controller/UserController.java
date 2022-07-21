package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.UserDto.DuplicateRequestDto;
import com.sparta.meeting_platform.dto.UserDto.LoginRequestDto;
import com.sparta.meeting_platform.dto.UserDto.ProfileRequestDto;
import com.sparta.meeting_platform.dto.UserDto.SignupRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //유저네임 중복 체크
    @PostMapping("/user/duplicate/username")
    public ResponseEntity<FinalResponseDto<?>> duplicateUsername (@Valid @RequestBody DuplicateRequestDto requestDto){
        return userService.duplicateUsername(requestDto);
    }

    //회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<FinalResponseDto<?>> signup (@Valid @RequestBody SignupRequestDto requestDto) {
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
            @Valid @RequestPart(value = "profileDto") ProfileRequestDto requestDto,
            @RequestPart(value = "profileImg", required = false) MultipartFile file) {
        return userService.setProfile(userDetails.getUser().getId(), requestDto, file);
    }

    // 회원 탈퇴
   @DeleteMapping("/user")
    public ResponseEntity<FinalResponseDto<?>> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(userDetails.getUser().getId());
    }

    // 프로필 페이지 이동
    @GetMapping("/user/profile")
    public ResponseEntity<FinalResponseDto<?>> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getProfile(userDetails.getUser().getId());
    }

    // 만료된 access token 재 발급
    @PostMapping(value = "/user/refresh")
    public ResponseEntity<FinalResponseDto<?>> refreshToken(
            @RequestHeader(value="Authorization") String accessToken,
            @RequestHeader(value="RefreshToken") String refreshToken ) {

        return userService.refreshToken(accessToken, refreshToken);
    }


}
