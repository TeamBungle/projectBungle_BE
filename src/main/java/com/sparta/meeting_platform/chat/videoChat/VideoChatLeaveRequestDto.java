package com.sparta.meeting_platform.chat.videoChat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VideoChatLeaveRequestDto {

    private Long postId;
    private String token;

}
