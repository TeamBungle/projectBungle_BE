package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatListMessageDto {
    private List<ChatRoomResponseDto> messageDto;
    private boolean isOwner;

    public ChatListMessageDto(List<ChatRoomResponseDto> chatRoomResponseDtoList, Boolean isOwner) {
        this.messageDto = chatRoomResponseDtoList;
        this.isOwner = isOwner;
    }
}
