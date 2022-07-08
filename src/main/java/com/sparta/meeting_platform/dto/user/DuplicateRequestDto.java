package com.sparta.meeting_platform.dto.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class DuplicateRequestDto {
    @NotNull(message = "아이디는 공백이 될 수 없습니다.")
    @Pattern(regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?", message = "아이디는 이메일 형식이여야 합니다.")
    private String username;
}
