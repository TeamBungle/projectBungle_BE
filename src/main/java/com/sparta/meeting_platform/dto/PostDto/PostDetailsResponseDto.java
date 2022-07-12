package com.sparta.meeting_platform.dto.PostDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostDetailsResponseDto {

    private String title;

    private String content;

    private String time;

    private int personnel;

    private String place;

    private List<String> postUrls;

    private List<String> tags;

    private List<String> categories;

    private int bungCount;

    private int mannerTemp;

    private List<String> joinPeopleUrl;

    private List<String> joinPeopleNickname;

    private int joinCount;

    private Boolean isLetter;

    private Boolean isLike = false;

    private Double latitude;

    private Double longitude;

    @Builder
    public PostDetailsResponseDto(String title,String content, String time, int personnel, String place, List<String> postUrls,
                                  List<String> tags, List<String> categories, int bungCount,
                                  int mannerTemp, List<String> joinPeopleUrl, List<String> joinPeopleNickname,
                                  int joinCount, Boolean isLetter, Boolean isLike ,Double latitude ,Double longitude) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.personnel = personnel;
        this.place = place;
        this.postUrls = postUrls;
        this.tags = tags;
        this.categories = categories;
        this.bungCount = bungCount;
        this.mannerTemp = mannerTemp;
        this.joinPeopleUrl = joinPeopleUrl;
        this.joinPeopleNickname = joinPeopleNickname;
        this.joinCount = joinCount;
        this.isLetter = isLetter;
        this.isLike = isLike;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
