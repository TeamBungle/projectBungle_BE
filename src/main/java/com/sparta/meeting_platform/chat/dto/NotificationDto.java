package com.sparta.meeting_platform.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDto {
    String message;
    String nickname;
    LocalDateTime createdAt;
    String roomId;
}
