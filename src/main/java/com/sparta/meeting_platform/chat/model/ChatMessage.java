package com.sparta.meeting_platform.chat.model;

import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅, 나가기
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String roomId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;
    @Column(nullable = false)
    private String sender;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private String profileUrl;
    @Column
    private Long enterUserCnt;
    @Column(nullable = false)
    private Long userId ;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column
    private String fileUrl;
    @Column
    private Boolean quitOwner = false;

    @JoinColumn(name = "CHAT_ROOM_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    public ChatMessage(ChatMessageDto chatMessageDto, ChatRoom chatRoom, LocalDateTime createdAt) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
        this.sender = chatMessageDto.getSender();
        this.profileUrl = chatMessageDto.getProfileUrl();
        this.enterUserCnt = chatMessageDto.getEnterUserCnt();
        this.userId = chatMessageDto.getUserId();
        this.createdAt = createdAt;
        this.fileUrl = chatMessageDto.getFileUrl();
        this.chatRoom = chatRoom;
    }
}
