package com.sparta.meeting_platform.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    // 메시지 타입
    private MessageType type;
    // 채팅방 ID
    private String roomId;
    // 내용
    private String message;
    // 보내는 사람
    private String nickName;
    // 프로필 사진
    private String profileUrl;
    // 채팅방 인원수, 채팅방 내에서 메세지가 전달될때 인원수 갱신시 사용
    private long userCount;

}
