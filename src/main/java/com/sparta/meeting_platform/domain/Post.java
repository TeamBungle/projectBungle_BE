package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostTestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Post {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "USER_ID")
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private int personnel;

    @Column(nullable = false)
    private String place;

    @ElementCollection
    @CollectionTable
    @Column(name = "category")
    private List<String> categories;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private Boolean isLetter;

    @ElementCollection
    @CollectionTable
    @Column(name = "postUrl")
    private List<String> postUrls;

    @ElementCollection
    @CollectionTable
    @Column(name = "tag")
    private List<String> tags;

    @Column
    private Point location;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    public Post(User user, PostTestDto requestDto,Double longitude,Double latitude, Point location){
        this.user = user;
        this.title = requestDto.getTitle();
        this.time = requestDto.getTime();
        this.personnel = requestDto.getPersonnel();
        this.place = requestDto.getPlace();
        this.isLetter = requestDto.getIsLetter();
        this.categories = requestDto.getCategories();
        this.tags = requestDto.getTags();
        this.postUrls = requestDto.getPostUrls();
        this.location = location;
        this.latitude= latitude;
        this.longitude = longitude;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.time = requestDto.getTime();
        this.personnel = requestDto.getPersonnel();
        this.place = requestDto.getPlace();
        this.isLetter = requestDto.getIsLetter();
        this.categories = requestDto.getCategories();
        this.tags = requestDto.getTags();
        this.postUrls = requestDto.getPostUrls();
//        this.longitude = requestDto.getLongitude();
//        this.latitude = requestDto.getLatitude();
        this.modifiedAt = LocalDateTime.now();
    }
}
