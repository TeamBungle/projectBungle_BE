package com.sparta.meeting_platform.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeRequestDto {
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
