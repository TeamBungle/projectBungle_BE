package com.sparta.meeting_platform.chat.controller;


import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.dto.FilesDto;
import com.sparta.meeting_platform.chat.dto.UserDetailDto;
import com.sparta.meeting_platform.chat.dto.UserinfoDto;
import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.service.ChatService;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping({"/message"})
    public void message(ChatMessageDto message, @Header("PK") Long userPk) {
        chatService.save(message, userPk);
    }

    //이전 채팅 기록 조회
    @GetMapping("/message/{roomId}")
    @ResponseBody
    public List<ChatMessageDto> getMessage(@PathVariable String roomId) {
        return chatMessageRepository.getMessage(roomId);
    }

    //채팅방에 파일 넣을때 url 빼오기
    @PostMapping("/message/file")
    @ResponseBody
    public String getMessage(@RequestPart(value = "file") MultipartFile file,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.getFileUrl(file, userDetails);
    }

    //채팅방에 참여한 사용자 정보 조회
    @GetMapping("/message/userinfo/{roomId}")
    @ResponseBody
    public List<UserinfoDto> getUserInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.getUserinfo(userDetails, roomId);
    }

    //햄버거 버튼 눌렀을때, fileUrl 들을 보낸다
    @GetMapping("/message/files/{roomId}")
    @ResponseBody
    public List<FilesDto> getFiles(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatService.getFiles(userDetails, roomId);
    }

    //유저 정보 상세 조회 (채팅방 안에서)
    @GetMapping("/details/{userId}")
    @ResponseBody
    public ResponseEntity<UserDetailDto> getUserDetails(@PathVariable Long userId) {
        return chatService.getUserDetails(userId);
    }
}

