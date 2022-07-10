package com.sparta.meeting_platform.chat.model;

import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import lombok.*;

import javax.persistence.*;

@Setter
@Builder
@AllArgsConstructor
@Getter
@Entity
@NoArgsConstructor
public class ChatMessage {

    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long Id;

    private MessageType type; // 메시지 타입
    private String roomId; // 방번호 (postId)
    private String sender; // nickname
    private String message; // 메시지
    private String profileUrl;
    private Long enterUserCnt;
    private String username;
    private String createdAt;

    public ChatMessage(ChatMessageDto chatMessageDto) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
    }
}
