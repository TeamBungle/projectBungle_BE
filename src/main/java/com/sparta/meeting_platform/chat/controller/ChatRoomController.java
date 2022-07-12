package com.sparta.meeting_platform.chat.controller;


import com.sparta.meeting_platform.chat.dto.ChatRoomResponseDto;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    // 내 채팅방 목록 반환
    @GetMapping("/rooms") // 내가보낸 마지막 메세지가 나옴 ;
    @ResponseBody
    public List<ChatRoomResponseDto> room(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userid = userDetails.getUser().getId();
        return chatRoomRepository.findAllRoom(userid);
    }
//    // 특정 채팅방 조회
//    @GetMapping("/room/{roomId}")
//    @ResponseBody
//    public ChatRoom roomInfo(@PathVariable String roomId) {
//        return chatRoomRepository.findRoomById(roomId);
//    }
}