package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.SettingDto.OpinionRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String message;

    @JoinColumn(name = "USER_ID")
    @ManyToOne
    private User user;

    public Opinion(User user, OpinionRequestDto opinionRequestDto) {
        this.message = opinionRequestDto.getMessage();
        this.user = user;
    }
}