package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.chat.model.ChatMessage;

public interface FindChatMessageDto {

    ChatMessage.MessageType getType();
    String getRoomId();
    String getSender();
    String getMessage();
    String getProfileUrl();
    Long getEnterUserCnt();
    Long getUserId();
    String getCreatedAt();
    String getFileUrl();
    Boolean getQuitOwner();
}
