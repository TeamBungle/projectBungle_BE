package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.user.SignUpRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor()
@Setter
@Entity(name = "userinfo")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column
    private String profileUrl;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String naverId;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int mannerTemp;

    @Column(nullable = false)
    private Boolean isOwner = false;

    @Column
    private String intro;

    @Column
    private int bungCount;





    public User(SignUpRequestDto requestDto,String userUrl) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.nickName = requestDto.getNickName();
        this.mannerTemp = 50;
        this.profileUrl = userUrl;
    }

    @Builder
    public User(String username, String password, String nickName, String profileUrl, Long kakaoId, String googleId,
                String naverId, LocalDateTime createdAt, int mannerTemp, Boolean isOwner, String intro, int bungCount) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.kakaoId = kakaoId;
        this.googleId = googleId;
        this.naverId = naverId;
        this.createdAt = createdAt;
        this.mannerTemp = mannerTemp;
        this.isOwner = isOwner;
        this.intro = intro;
        this.bungCount = bungCount;
    }

    public void setReport(){
        this.mannerTemp -= 5;
    }

}
