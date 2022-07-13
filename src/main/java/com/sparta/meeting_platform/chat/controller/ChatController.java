package com.sparta.meeting_platform.chat.controller;


import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.dto.FilesDto;
import com.sparta.meeting_platform.chat.dto.UserDetailDto;
import com.sparta.meeting_platform.chat.dto.UserinfoDto;
import com.sparta.meeting_platform.chat.service.ChatService;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping({"/chat/message"})
    public void message(ChatMessageDto message, @Header("token") String BearerToken) {
        log.info("요청 메서드 [message] /chat/message");
        chatService.save(message, BearerToken);
    }

    //이전 채팅 기록 조회
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessageDto> getMessage(@PathVariable String roomId) {
        log.info("요청 메서드 [GET] /chat/message/{roomId}");
        return chatService.getMessages(roomId);
    }

    //채팅방에 파일 넣을때 url 빼오기
    @PostMapping("/chat/message/file")
    @ResponseBody
    public String getMessage(@RequestPart(value = "file") MultipartFile file,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("요청 메서드 [POST] /chat/message/file");
        return chatService.getFileUrl(file, userDetails);
    }

    //채팅방에 참여한 사용자 정보 조회
    @GetMapping("/chat/message/userinfo/{roomId}")
    @ResponseBody
    public List<UserinfoDto> getUserInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("요청 메서드 [GET] /chat/message/userinfo");
        return chatService.getUserinfo(userDetails, roomId);
    }
    //햄버거 버튼 눌렀을때, fileUrl들을 보낸다
    @GetMapping("/chat/message/files/{roomId}")
    @ResponseBody
    public List<FilesDto> getFiles(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("요청 메서드 [GET] /chat/message/userinfo");
        return chatService.getFiles(userDetails, roomId);
    }

    //유저 정보 상세 조회 (채팅방 안에서)
    @GetMapping("/chat/details/{userId}")
    @ResponseBody
    public ResponseEntity<UserDetailDto> getUserDetails(@PathVariable Long userId) {
        return chatService.getUserDetails(userId);
    }
}

