package com.sparta.meeting_platform.util;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Location {

    private Double latitude;
    private Double longitude;

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
