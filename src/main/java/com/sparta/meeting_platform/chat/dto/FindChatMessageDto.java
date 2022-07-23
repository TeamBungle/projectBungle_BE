package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.chat.model.ChatMessage;

import java.util.Date;

public interface FindChatMessageDto {

    ChatMessage.MessageType getType();
    String getRoomId();
    String getSender();
    String getMessage();
    String getProfileUrl();
    Long getEnterUserCnt();
    Long getUserId();
    Date getCreatedAt();
    String getFileUrl();
    Boolean getQuitOwner();
}
