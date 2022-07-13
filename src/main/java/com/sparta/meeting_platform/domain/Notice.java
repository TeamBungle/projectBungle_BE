package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.NoticeRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    public Notice(NoticeRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.createdAt = requestDto.getCreatedAt();
    }
}
