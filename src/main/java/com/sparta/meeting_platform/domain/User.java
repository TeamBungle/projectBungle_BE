package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.user.SignUpRequestDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(nullable = false)
    private String profileUrl;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private Long googleId;

    @Column(unique = true)
    private String naverId;

    @Column(nullable = false)
    private Float mannerTemp;

    @CreatedDate
    private LocalDateTime createdAt;

    public User(SignUpRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.nickName = requestDto.getNickName();
        this.profileUrl = requestDto.getIconUrl();
        this.mannerTemp = 36.5f;
    }

}
