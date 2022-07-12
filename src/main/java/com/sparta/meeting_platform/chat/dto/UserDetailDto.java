package com.sparta.meeting_platform.chat.dto;

import com.sparta.meeting_platform.domain.User;

public class UserDetailDto {
    private String nickname;
    private String profileUrl;
    private int bungCount;
    private int mannerTemp;
    private String intro;
    private Boolean response;
    private String message;

    public UserDetailDto(Boolean response, String message, User user) {
        this.response = response;
        this.message = message;
        this.nickname = user.getNickName();
        this.profileUrl = user.getProfileUrl();
        this.bungCount = user.getBungCount();
        this.mannerTemp = user.getMannerTemp();
        this.intro = user.getIntro();
    }
}
