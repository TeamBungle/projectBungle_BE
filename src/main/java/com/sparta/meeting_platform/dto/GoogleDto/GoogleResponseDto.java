package com.sparta.meeting_platform.dto.GoogleDto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleResponseDto {
    private Boolean success;
    private String message;
    private String nickname;
    private Float mannerTemp;
}
