package com.sparta.meeting_platform.chat.controller;

import com.sparta.meeting_platform.chat.dto.ChatRoomResponseDto;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    // 내 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomResponseDto> room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return chatRoomRepository.findAllRoom(user);
    }
    // 특정 채팅방 입장
    @PostMapping("/room/{postId}")
    @ResponseBody
    public String roomInfo(@PathVariable Long postId) {
        return String.valueOf(postId);
    }



}