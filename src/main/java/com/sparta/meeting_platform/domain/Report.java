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

    @JoinColumn(name = "REPORTER_ID")
    @ManyToOne
    private User user;

    @Column(nullable = false, name = "BAD_MANNER_ID")
    private Long badMannerId;

    @CreatedDate
    private LocalDateTime createdAt;


    public Report(User user, Long badMannerId) {
        this.user = user;
        this.badMannerId = badMannerId;
        this.createdAt = LocalDateTime.now();
    }
}
