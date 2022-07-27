package com.sparta.meeting_platform.dto.SettingDto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeRequestDto {
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
