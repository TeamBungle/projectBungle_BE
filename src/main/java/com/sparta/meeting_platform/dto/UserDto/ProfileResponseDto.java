package com.sparta.meeting_platform.dto.UserDto;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileResponseDto {

    private String nickName;
    private String intro;
    private String profileUrl;

    public ProfileResponseDto(User user){
        this.nickName = user.getNickName();
        this.intro = user.getIntro();
        this.profileUrl = user.getProfileUrl();
    }

}

