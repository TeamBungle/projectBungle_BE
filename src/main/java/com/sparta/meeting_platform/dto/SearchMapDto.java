package com.sparta.meeting_platform.dto;

import lombok.Getter;

@Getter
public class SearchMapDto {
    private Double latitude;
    private Double longitude;

    public SearchMapDto(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
