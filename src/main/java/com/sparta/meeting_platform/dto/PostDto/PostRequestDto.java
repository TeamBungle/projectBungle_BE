package com.sparta.meeting_platform.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private String time;

    @Pattern(regexp = "^.{1,100}$", message = "아이디는 이메일 형식이여야 합니다.")
    private String content;

    private int personnel;
    private String place;
    private List<String> tags;
    private List<String> categories;
    private List<String> postUrls;
    private Double latitude;
    private Double longitude;
    private Boolean isLetter;
}