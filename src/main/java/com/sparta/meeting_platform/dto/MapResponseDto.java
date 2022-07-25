package com.sparta.meeting_platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import java.util.List;

@NoArgsConstructor
@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapResponseDto<T> {
    private boolean response;
    private String message;
    private List<MapListDto> mapListDtos;
    boolean isOwner;

    public MapResponseDto(boolean response, String message, List<MapListDto> mapListDtos, boolean isOwner) {
        this.response = response;
        this.message = message;
        this.mapListDtos = mapListDtos;
        this.isOwner = isOwner;
    }
}