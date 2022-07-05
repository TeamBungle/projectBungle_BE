package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@NoArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapListDto {
    private Long id;

    private String title;

    private String time;

    private int personnel;

    private String place;

    private List<String> postUrls;

    private int joinCount;

    private Boolean isLetter;

    private Boolean isLike = false;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int avgTemp;

    private String postUrl;

    private Double latitude;

    private Double longitude;

    @Builder
    public MapListDto(Long id, String title, String time, int personnel, String place, List<String> postUrls,
                      int joinCount, Boolean isLetter, Boolean isLike,int avgTemp,
                      String postUrl, Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.personnel = personnel;
        this.place = place;
        this.postUrls = postUrls;
        this.joinCount = joinCount;
        this.isLetter = isLetter;
        this.isLike = isLike;
        this.avgTemp = avgTemp;
        this.postUrl = postUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
