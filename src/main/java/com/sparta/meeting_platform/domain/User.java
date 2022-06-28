package com.sparta.meeting_platform.domain;

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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String profileUrl;

    @Column(unique = true)
    private String kakaoId;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String naverId;

    @Column(nullable = false)
    private Float mannerTemp;

    @CreatedDate
    private LocalDateTime cteatedAt;

}
