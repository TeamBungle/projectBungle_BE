package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.user.LoginRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.QrcodeCheckService;
import com.sparta.meeting_platform.service.SocialGoogleService;
import com.sparta.meeting_platform.service.SocialKakaoService;
import com.sparta.meeting_platform.service.SocialNaverService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class QrcodeController {

    private final QrcodeCheckService qrcodeCheckService;


    @GetMapping("/qrcode")
    public Object createQr(@RequestParam Long postId) throws WriterException, IOException {
        String url = "https://localhost:3000/uesr/qrcode?postId="+postId;
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

//    @GetMapping("/uesr/qrcode")
//    public String qrcodeCheck (@RequestParam Long postId,
//                               @AuthenticationPrincipal UserDetailsImpl userDetails){
//        qrcodeCheckService.qrcodeUserCheck(postId,userDetails.getUser().getId());
//        return "redirect:http://localhost:3000/main";
//    }

    @PostMapping("/user/qrcode/normal")
    public ResponseEntity<FinalResponseDto<?>> qrcodeCheck(@RequestBody LoginRequestDto requestDto,
                                                           @RequestParam Long postId){
        return qrcodeCheckService.qrcodeUserCheck(postId,requestDto);
    }

    @GetMapping("/user/qrcode/google")
    public ResponseEntity<FinalResponseDto<?>> googleLogin(@RequestParam(value = "code") String authCode,
                                                           @RequestParam(value = "postId") Long postId,
                                                           HttpServletResponse httpServletResponse) throws JsonProcessingException {
        return qrcodeCheckService.googleLogin(authCode, httpServletResponse,postId);
    }

    @GetMapping("/user/qrcode/kakao")
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(@RequestParam(value = "code") String code,
                                                          @RequestParam(value = "postId") Long postId,
                                                          HttpServletResponse response) throws JsonProcessingException {
        return qrcodeCheckService.kakaoLogin(code, response,postId);
    }
    @GetMapping("/user/qrcode/naver")
    public ResponseEntity<FinalResponseDto<?>> naverLogin(@RequestParam String code, @RequestParam String state,
                                                          @RequestParam(value = "postId") Long postId,
                                                          HttpServletResponse response) throws IOException {
        return qrcodeCheckService.naverLogin(code, state, response,postId);
    }
}
