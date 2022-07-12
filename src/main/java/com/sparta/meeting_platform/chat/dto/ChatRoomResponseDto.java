package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
    private String lastMessage;
    private String postUrl;
    private String postTitle;
    private String lastMessageTime;
    private boolean isLetter;
    private LocalDateTime postCreatedAt;
}
