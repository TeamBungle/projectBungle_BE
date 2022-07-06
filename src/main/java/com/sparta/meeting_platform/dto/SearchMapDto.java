package com.sparta.meeting_platform.dto;

import lombok.Getter;

@Getter
public class SearchMapDto {

    private Double longi;
    private Double lati;

    public SearchMapDto(Double longi, Double lati) {
        this.longi = longi;
        this.lati = lati;
    }
}
