package com.sparta.meeting_platform.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private boolean response;
    private String message;
    private String nickName;
    private Float mannerTemp;


    public ResponseDto(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public ResponseDto(boolean response, String message, String nickname , float mannerTemp) {
        this.response = response;
        this.message = message;
        this.nickName = nickname;
        this.mannerTemp = mannerTemp;
    }
}
