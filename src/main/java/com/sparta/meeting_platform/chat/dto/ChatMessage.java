package com.sparta.meeting_platform.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@Getter
public class ChatMessage {


    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    private MessageType type; // 메시지 타입
    private Long postId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private Long userCount;
}
