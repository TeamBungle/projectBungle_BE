package com.sparta.meeting_platform.dto.PostDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private Long id;

    private String title;

    private String content;

    private String time;

    private int personnel;

    private String place;

    private List<String> postUrls;

    private List<String> tags;

    private List<String> categories;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int bungCount;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int mannerTemp;

    private List<String> joinPeopleUrl;

    private List<String> joinPeopleNickname;

    private int joinCount;

    private Boolean isLetter;

    private Boolean isLike = false;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int avgTemp;

    private String postUrl;

    @Builder
    public PostResponseDto(Long id, String title, String content,String time, int personnel, String place, List<String> postUrls,
                           List<String> tags, List<String> categories, int bungCount, int mannerTemp, List<String> joinPeopleUrl,
                           List<String> joinPeopleNickname, int joinCount, Boolean isLetter, Boolean isLike,
                           int avgTemp, String postUrl) {
        this.id = id;
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
        this.avgTemp = avgTemp;
        this.postUrl = postUrl;
    }

//    public PostResponseDto(Post post, String time) {
//        this.id = post.getId();
//        this.title = post.getTitle();
//        this.time = time;
//        this.personnel = post.getPersonnel();
//        this.place = post.getPlace();
//        this.postUrls = post.getPostUrls();
//        this.tags = post.getTags();
//        this.categories = post.getCategories();
//        this.isLetter = post.getIsLetter();
//    }
//
//    @Builder
//    public PostResponseDto(Post post, Boolean isLike, String time) {
//        this.title = post.getTitle();
//        this.time = time;
//        this.personnel = post.getPersonnel();
//        this.place = post.getPlace();
//        this.postUrls = post.getPostUrls();
//        this.tags = post.getTags();
//        this.categories = post.getCategories();
//        this.bungCount = post.getUser().getBungCount();;
//        this.mannerTemp = post.getUser().getMannerTemp();
//        this.joinPeopleUrl = null;              //수정필요
//        this.joinPeopleNickname = null;         //수정필요
//        this.joinCount = 1;
//        this.isLetter = post.getIsLetter();
//        this.isLike = isLike;
//    }

}
