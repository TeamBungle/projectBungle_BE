package com.sparta.meeting_platform.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ResignChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String roomId; // 방번호 (postId)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessage.MessageType type; // 메시지 타입
    @Column(nullable = false)
    private String sender; // nickname
    @Column(nullable = false)
    private String message; // 메시지
    @Column(nullable = false)
    private String profileUrl;
    @Column
    private Long enterUserCnt;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column
    private String fileUrl;

    public ResignChatMessage(ChatMessage chatMessage) {
        this.roomId = chatMessage.getRoomId();
        this.type = chatMessage.getType();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
        this.profileUrl = chatMessage.getProfileUrl();
        this.enterUserCnt = chatMessage.getEnterUserCnt();
        this.userId = chatMessage.getUserId();
        this.createdAt = chatMessage.getCreatedAt();
        this.fileUrl = chatMessage.getFileUrl();
    }
}
