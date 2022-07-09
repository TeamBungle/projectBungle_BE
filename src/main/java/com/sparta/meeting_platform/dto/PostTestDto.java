package com.sparta.meeting_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostTestDto {

    private String title;
    private String time;
    private int personnel;
    private Boolean isLetter;
    private String place;
    private List<String> tags;
    private List<String> categories;
    private List<String> postUrls;
}