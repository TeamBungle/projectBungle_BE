package com.sparta.meeting_platform.dto.NaverDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class NaverUserDto {

    private String naverId;
    private String email;
    private String nickName;
    private String profileUrl;
}
