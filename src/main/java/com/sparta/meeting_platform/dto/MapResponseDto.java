package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;

@NoArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapResponseDto<T> {
    private boolean response;
    private String message;

    private Long id;

    private String title;

    private String time;

    private int personnel;

    private String place;

    private Boolean isLetter;

    private Boolean isLike;
    private Float latitude;
    private Float longitude;

    public MapResponseDto(boolean response, String message, Float id, Float ed){
        this.response = response;
        this.message = message;
        this.latitude = id;
        this.longitude = ed;
    }
}