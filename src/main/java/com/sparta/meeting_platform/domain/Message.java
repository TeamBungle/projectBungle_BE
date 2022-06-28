package com.sparta.meeting_platform.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Message {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "POST_ID")
    @ManyToOne
    private Post post;

    @JoinColumn(name = "USER_ID")
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String message;

    @CreatedDate                // 상속 받을것인지? 그냥 낱개로 쓸것인지
    @Column(updatable = false)
    private LocalDateTime createDate;

}
