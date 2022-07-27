package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.UserDto.LoginRequestDto;
import com.sparta.meeting_platform.service.QrcodeCheckService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class QrcodeController {

    private final QrcodeCheckService qrcodeCheckService;

    //qr코드 제작
    @GetMapping("/qrcode")
    public Object createQr(@RequestParam String roomId) throws WriterException, IOException {
        String url = "https://localhost:3000/user/qrcode?roomId=" + roomId;
        int width = 200;
        int height = 200;
        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        }
    }

    //qr코드 일반로그인 유저 인증
    @PostMapping("/user/qrcode/normal")
    public ResponseEntity<FinalResponseDto<?>> qrcodeCheck(@RequestBody LoginRequestDto requestDto,
                                                           @RequestParam Long postId) {
        return qrcodeCheckService.qrcodeUserCheck(postId, requestDto);
    }

    //qr코드 구글로그인 유저 인증
    @GetMapping("/user/qrcode/google")
    public ResponseEntity<FinalResponseDto<?>> googleLogin(
            @RequestParam(value = "code") String authCode,
            @RequestParam(value = "postId") Long postId) throws JsonProcessingException {
        return qrcodeCheckService.googleLogin(authCode, postId);
    }

    //qr코드 카카오로그인 유저 인증
    @GetMapping("/user/qrcode/kakao")
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "postId") Long postId) throws JsonProcessingException {
        return qrcodeCheckService.kakaoLogin(code, postId);
    }

    //qr코드 네이버로그인 유저 인증
    @GetMapping("/user/qrcode/naver")
    public ResponseEntity<FinalResponseDto<?>> naverLogin(
            @RequestParam String code, @RequestParam String state,
            @RequestParam(value = "postId") Long postId) {
        return qrcodeCheckService.naverLogin(code, state, postId);
    }
}
