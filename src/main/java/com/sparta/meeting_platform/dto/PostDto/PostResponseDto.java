package com.sparta.meeting_platform.dto.PostDto;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;

    private String time;

    private int personnel;

    private String place;

    private List<String> postUrls;

    private List<String> tags;

    private List<String> categories;

    private Boolean isLetter;

    private Boolean isLike = false;



    public PostResponseDto(Post post, String time) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.time = time;
        this.personnel = post.getPersonnel();
        this.place = post.getPlace();
        this.postUrls = post.getPostUrls();
        this.tags = post.getTags();
        this.categories = post.getCategories();
        this.isLetter = post.getIsLetter();
    }

    public PostResponseDto(Post post, Boolean isLike, String time) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.time = time;
        this.personnel = post.getPersonnel();
        this.place = post.getPlace();
        this.postUrls = post.getPostUrls();
        this.tags = post.getTags();
        this.categories = post.getCategories();
        this.isLetter = post.getIsLetter();
        this.isLike = isLike;
    }


}
