package com.sparta.meeting_platform.dto.user;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;

@Getter
public class MyPageDto {

    private final String nickName;
    private final int mannerTemp;
    private final String intro;
    private final String profileUrl;
    private final int bungCount;

    public MyPageDto(User user) {
        this.nickName = user.getNickName();
        this.mannerTemp = user.getMannerTemp();
        this.intro = user.getIntro();
        this.profileUrl = user.getProfileUrl();
        this.bungCount = user.getBungCount();
    }
}
