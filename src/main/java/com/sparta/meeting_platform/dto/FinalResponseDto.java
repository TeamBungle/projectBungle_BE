package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.meeting_platform.dto.PostDto.PostDetailsResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.user.MyPageDto;
import com.sparta.meeting_platform.dto.user.ProfileResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

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
    private Boolean isOwner;
    private PostResponseDto postResponseDto;
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

    public FinalResponseDto(boolean response, String message, Long postId) {
        this.response = response;
        this.message = message;
        this.postId = postId;
    }



    public FinalResponseDto(boolean response, String message, PostDetailsResponseDto postDetailsResponseDto) {
        this.response = response;
        this.message = message;
        this.postDetailsResponseDto = postDetailsResponseDto;
    }

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

    public FinalResponseDto(boolean response, String message, ProfileResponseDto profileResponseDto) {
        this.response = response;
        this.message = message;
        this.profileResponseDto = profileResponseDto;
    }
}
