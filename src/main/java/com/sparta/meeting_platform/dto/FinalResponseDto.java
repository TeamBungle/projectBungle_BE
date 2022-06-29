package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
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

    private Float mannerTemp;

    public FinalResponseDto(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public FinalResponseDto(boolean response, String message, List<T> postList) {
        this.response = response;
        this.message = message;
        this.List = postList;
    }

    public FinalResponseDto(boolean response, String message, PostResponseDto postResponseDto) {
        this.response = response;
        this.message = message;
        this.postResponseDto = postResponseDto;
    }

    public FinalResponseDto(boolean response, String message, String nickname , float mannerTemp) {
        this.response = response;
        this.message = message;
        this.nickName = nickname;
        this.mannerTemp = mannerTemp;
    }
}
