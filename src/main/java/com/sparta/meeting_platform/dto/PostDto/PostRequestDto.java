package com.sparta.meeting_platform.dto.PostDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private String time;
//    @Pattern(regexp = "^.{0,500}$", message = "모임내용은 500자 이내에 작성해 주세요")
    private String content;
    private int personnel;
    private String place;
    private List<String> tags;
    private List<String> categories;
    private List<String> postUrls;
    private Double latitude;
    private Double longitude;
    private Boolean isLetter;
}