package com.sparta.meeting_platform.chat.videoChat;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VideoChatResponseDto {

    private String token;
    private Long userId;
    private String profileUrl;
    private String nickName;
    public VideoChatResponseDto(String token, User user){
        this.token = token;
        this.userId = user.getId();
        this.profileUrl = user.getProfileUrl();
        this.nickName = user.getNickName();
    }

}
