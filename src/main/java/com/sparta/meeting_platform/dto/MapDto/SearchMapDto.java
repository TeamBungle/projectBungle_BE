package com.sparta.meeting_platform.dto.MapDto;

import lombok.Getter;

@Getter
public class SearchMapDto {
    private Double latitude;
    private Double longitude;

    public SearchMapDto( Double longitude,Double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
