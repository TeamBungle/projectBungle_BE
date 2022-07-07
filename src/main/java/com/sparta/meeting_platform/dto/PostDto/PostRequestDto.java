package com.sparta.meeting_platform.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private String time;
    private int personnel;
    private String place;
    private List<String> tags;
    private List<String> categories;
    private List<String> postUrls;
    private Double latitude;
    private Double longitude;
    private Boolean isLetter;
}