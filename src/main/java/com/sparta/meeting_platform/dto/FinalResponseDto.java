package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.user.MyPageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@NoArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinalResponseDto<T> {

    private List<T> List;
    private boolean response;
    private String message;
    private PostResponseDto postResponseDto;
    private String nickName;
    private int mannerTemp;

    private MyPageDto myPageDto;

    public FinalResponseDto(boolean response, String message, MyPageDto myPageDto) {
        this.response = response;
        this.message = message;
        this.myPageDto = myPageDto;
    }

    public FinalResponseDto(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public FinalResponseDto(boolean response, String message, List<T> List) {
        this.response = response;
        this.message = message;
        this.List = List;
    }

    public FinalResponseDto(boolean response, String message, PostResponseDto postResponseDto) {
        this.response = response;
        this.message = message;
        this.postResponseDto = postResponseDto;
    }

    public FinalResponseDto(boolean response, String message, String nickname, int mannerTemp) {
        this.response = response;
        this.message = message;
        this.nickName = nickname;
        this.mannerTemp = mannerTemp;
    }
}
