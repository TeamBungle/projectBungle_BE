package com.sparta.meeting_platform.chat.dto;


import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private String username;
    private String password;
    private String nickName;
    private String profileUrl;
    private Long kakaoId;
    private String googleId;
    private String naverId;
    private int mannerTemp;
    private Boolean isOwner = false;
    private String intro;
    private int bungCount;
    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickName = user.getNickName();
        this.profileUrl = user.getProfileUrl();
        this.kakaoId = user.getKakaoId();
        this.googleId = user.getGoogleId();
        this.naverId = user.getNaverId();
        this.mannerTemp = user.getMannerTemp();
        this.isOwner = user.getIsOwner();
        this.intro = user.getIntro();
        this.bungCount = user.getBungCount();
    }
}
