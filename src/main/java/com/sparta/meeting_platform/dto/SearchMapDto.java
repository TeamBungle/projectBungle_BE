package com.sparta.meeting_platform.dto;

import lombok.Getter;

@Getter
public class SearchMapDto {
    private Double latitude;
    private Double longitude;

    public SearchMapDto(Double longitude, Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
