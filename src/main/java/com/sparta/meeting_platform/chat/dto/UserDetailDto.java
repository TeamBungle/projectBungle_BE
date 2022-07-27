package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailDto {
    private String nickname;
    private String profileUrl;
    private int bungCount;
    private int mannerTemp;
    private String intro;
    private Boolean response;
    private String message;

    private Boolean chatOwner;
    public UserDetailDto(Boolean response, String message, User user, Boolean chatOwner) {
        this.response = response;
        this.message = message;
        this.nickname = user.getNickName();
        this.profileUrl = user.getProfileUrl();
        this.bungCount = user.getBungCount();
        this.mannerTemp = user.getMannerTemp();
        this.intro = user.getIntro();
        this.chatOwner =chatOwner;
    }
}
