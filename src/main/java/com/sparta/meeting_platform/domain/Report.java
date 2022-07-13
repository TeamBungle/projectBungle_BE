package com.sparta.meeting_platform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor()
@Setter
@Entity
public class Report {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long reporterId;

    @Column(nullable = false)
    private Long badMannerId;

    @Column
    private String badMannerNickName;

    @Column
    private String badMannerUrl;


    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String history;


    public Report(Long reporterId, Long badMannerId, String badMannerNickName, String badMannerUrl, String history) {
        this.reporterId = reporterId;
        this.badMannerId = badMannerId;
        this.badMannerNickName = badMannerNickName;
        this.badMannerUrl = badMannerUrl;
        this.createdAt = LocalDateTime.now();
        this.history = history;
    }
}
