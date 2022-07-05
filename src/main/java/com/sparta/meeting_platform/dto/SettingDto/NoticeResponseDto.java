package com.sparta.meeting_platform.dto.SettingDto;

import com.sparta.meeting_platform.domain.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeResponseDto {

    private LocalDateTime createdAt;
    private String title;
    private String content;

    public NoticeResponseDto(Notice notice) {
        this.createdAt = notice.getCreatedAt();
        this.title = notice.getTitle();
        this.content = notice.getContent();
    }
}
