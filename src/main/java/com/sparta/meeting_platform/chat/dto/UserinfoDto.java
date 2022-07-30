package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserinfoDto {
    private String nickname;
    private String profileUrl;
    private Long userId;

    public UserinfoDto(String nickname, String profileUrl, Long userId) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.userId = userId;
    }
}
