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
    @Column
    private String roomId; // 방번호 (postId)
    @Enumerated(EnumType.STRING)
    @Column
    private MessageType type; // 메시지 타입
    @Column
    private String sender; // nickname
    @Column
    private String message; // 메시지
    @Column
    private String profileUrl;
    @Column
    private Long enterUserCnt;
    @Column
    private String username;
    @Column
    private String createdAt;
    @Column
    private String fileUrl;

    @JoinColumn(name = "CHAT_ROOM_ID")
    @ManyToOne
    private ChatRoom chatRoom;


    public ChatMessage(ChatMessageDto chatMessageDto,ChatRoom chatRoom) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
        this.sender = chatMessageDto.getSender();
        this.profileUrl = chatMessageDto.getProfileUrl();
        this.enterUserCnt = chatMessageDto.getEnterUserCnt();
        this.username = chatMessageDto.getUsername();
        this.createdAt = chatMessageDto.getCreatedAt();
        this.fileUrl = chatMessageDto.getFileUrl();
        this.chatRoom = chatRoom;
    }
}
