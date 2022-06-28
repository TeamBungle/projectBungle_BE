package com.sparta.meeting_platform.dto.KakaoDto;

import lombok.Getter;

@Getter
public class KakaoUserInfoDto {
    private Long KakaoId;
    private String nickname;
    private String email;
    private String profileUrl;

    private String message;

    private boolean response;

    public KakaoUserInfoDto(boolean response,String message) {
        this.message = message;
        this.response = response;
    }

    public KakaoUserInfoDto(Long KakaoId, String nickname, String email, String profileUrl) {
        this.KakaoId = KakaoId;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
    }
}