package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.chat.model.ChatMessage;
import java.time.LocalDateTime;

public interface FindChatMessageDto {

    ChatMessage.MessageType getType();
    String getRoomId();
    String getSender();
    String getMessage();
    String getProfileUrl();
    Long getEnterUserCnt();
    Long getUserId();
    LocalDateTime getCreatedAt();
    String getFileUrl();
    Boolean getQuitOwner();
}
