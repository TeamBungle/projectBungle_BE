package com.sparta.meeting_platform.controller;



import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.user.DuplicateRequestDto;
import com.sparta.meeting_platform.dto.user.LoginRequestDto;
import com.sparta.meeting_platform.dto.user.SignUpRequestDto;
import com.sparta.meeting_platform.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<FinalResponseDto<?>> signup (@RequestPart(value = "signupDto") SignUpRequestDto requestDto,
                                                       @RequestPart(value = "userImg") MultipartFile file){
        return userService.signup(requestDto,file);
    }

    //로그인
    @PostMapping("/user/login")
    public ResponseEntity<FinalResponseDto<?>> login (@RequestBody LoginRequestDto requestDto) {
        return userService.login(requestDto);
    }
}
