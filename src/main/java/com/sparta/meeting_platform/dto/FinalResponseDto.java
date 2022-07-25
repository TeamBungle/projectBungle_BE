package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.UserDto.MyPageDto;
import com.sparta.meeting_platform.dto.UserDto.ProfileResponseDto;
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
    private List<String> postUrls;
    private boolean response;
    private String message;
    private Long userId;
    private Boolean isOwner;
    private PostResponseDto postResponseDto;
    private String username;
    private String nickName;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int mannerTemp;
    private MyPageDto myPageDto;
    private ProfileResponseDto profileResponseDto;
    private Long postId;
    private List<PostResponseDto> postListRealTime;
    private List<PostResponseDto> postListEndTime;
    private PostDetailsResponseDto postDetailsResponseDto;


    public FinalResponseDto(boolean response, String message, Long postId, List<String> postUrls) {
        this.response = response;
        this.message = message;
        this.postId = postId;
        this.postUrls = postUrls;
    }

    public FinalResponseDto(boolean response, String message, Boolean isOwner,
                            List<PostResponseDto> postListRealTime, List<PostResponseDto> postListEndTime){
        this.response = response;
        this.message = message;
        this.isOwner = isOwner;
        this.postListRealTime = postListRealTime;
        this.postListEndTime = postListEndTime;

    }

    public FinalResponseDto(boolean response, String message, Long postId, Long userId) {
        this.response = response;
        this.message = message;
        this.postId = postId;
        this.userId = userId;
    }



    public FinalResponseDto(boolean response, String message, PostDetailsResponseDto postDetailsResponseDto, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.postDetailsResponseDto = postDetailsResponseDto;
        this.isOwner = isOwner;
    }

    public FinalResponseDto(boolean response, String message, MyPageDto myPageDto, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.myPageDto = myPageDto;
        this.isOwner = isOwner;
    }

    public FinalResponseDto(boolean response, String message) {
        this.response = response;
        this.message = message;
    }

    public FinalResponseDto(boolean response, String message, List<T> List, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.List = List;
        this.isOwner = isOwner;
    }

    public FinalResponseDto(boolean response, String message, PostResponseDto postResponseDto, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.postResponseDto = postResponseDto;
        this.isOwner = isOwner;
    }

    public FinalResponseDto(boolean response, String message, String nickname, int mannerTemp, Long userId) {
        this.response = response;
        this.message = message;
        this.nickName = nickname;
        this.mannerTemp = mannerTemp;
        this.userId = userId;
    }

    public FinalResponseDto(boolean response, String message, ProfileResponseDto profileResponseDto, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.profileResponseDto = profileResponseDto;
        this.isOwner = isOwner;
    }

    public FinalResponseDto(boolean response, String message, Boolean isOwner) {
        this.response =response;
        this.message =message;
        this.isOwner =isOwner;
    }
}
