package com.sparta.meeting_platform.dto.UserDto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ProfileRequestDto {

    @NotNull(message = "닉네임은 공백이 될 수 없습니다.")
    @Pattern(regexp = "^[0-9A-Za-z가-힣]{4,20}$", message = "닉네임은 한글,영문,숫자 4~20자리여야 합니다.")
    private String nickName;

    @Pattern(regexp = "^.{1,20}$", message = "소개글은 20자 이내에 작성해 주세요")
    private String intro;

}
