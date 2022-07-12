package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.chat.model.ChatMessage;

public interface FindChatMessageDto {

    ChatMessage.MessageType getType();
    String getRoomId(); // 방번호 (postId)
    String getSender(); // nickname
    String getMessage(); // 메시지
    String getProfileUrl();
    Long getEnterUserCnt();
    String getUsername();
    String getCreatedAt();
    String getFileUrl();
}
