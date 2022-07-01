package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoomDto {

    private String roomId;
    private String title;

    // WebSocketSession 은 Spring 에서 Websocket Connection 이 맺어진 세션



}