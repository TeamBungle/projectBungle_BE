package com.sparta.meeting_platform.dto.MapDto;

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
    private Long postId;

    private String title;

    private String content;

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

//    @JsonIgnore
    private Double distance;

    @Builder
    public MapListDto(Long id, String title, String content, String time, int personnel, String place, List<String> postUrls,
                      int joinCount, Boolean isLetter, Boolean isLike,int avgTemp,
                      String postUrl, Double latitude, Double longitude,Double distance) {
        this.postId = id;
        this.title = title;
        this.content =content;
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
        this.distance = distance;
    }
}
