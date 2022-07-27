package com.sparta.meeting_platform.chat.dto;


import com.sparta.meeting_platform.chat.model.ChatMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private String message;
    private String sender;
    private String profileUrl;
    private Long enterUserCnt;
    private Long userId;
    private String createdAt;
    private String fileUrl;
    private Boolean quitOwner;

    public ChatMessageDto(FindChatMessageDto chatMessage,String createdAt) {
        this.type = chatMessage.getType();
        this.roomId = chatMessage.getRoomId();
        this.message =chatMessage.getMessage();
        this.sender = chatMessage.getSender();
        this.profileUrl = chatMessage.getProfileUrl();
        this.enterUserCnt = chatMessage.getEnterUserCnt();
        this.userId = chatMessage.getUserId();
        this.createdAt = createdAt;
        this.fileUrl = chatMessage.getFileUrl();
        this.quitOwner = chatMessage.getQuitOwner();
    }
}