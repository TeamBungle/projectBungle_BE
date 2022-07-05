package com.sparta.meeting_platform.dto.user;

import lombok.Getter;

@Getter
public class MyPageDto {

    private String nickName;
    private int mannerTemp;
    private String profileUrl;
    private int bungCount;

    public MyPageDto(String nickName, int mannerTemp, String profileUrl, int bungCount) {
        this.nickName = nickName;
        this.mannerTemp = mannerTemp;
        this.profileUrl = profileUrl;
        this.bungCount = bungCount;
    }
}
