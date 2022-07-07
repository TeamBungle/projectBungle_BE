package com.sparta.meeting_platform.chat.controller;


import com.sparta.meeting_platform.chat.dto.ChatMessage;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {




    private final ChatRoomRepository chatRoomRepository;


    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {

        chatRoomRepository.message(message);

    }
}
