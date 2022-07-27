package com.sparta.meeting_platform.dto.UserDto;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Setter
@Getter
public class ProfileResponseDto {

    private String nickName;
    private String intro;
    private String profileUrl;
    private int bungCount;
    private int mannerTemp;

    public ProfileResponseDto(User user){
        this.nickName = user.getNickName();
        this.intro = user.getIntro();
        this.profileUrl = user.getProfileUrl();
        this.mannerTemp = user.getMannerTemp();
        this.bungCount = user.getBungCount();
    }

}

