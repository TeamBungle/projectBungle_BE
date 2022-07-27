package com.sparta.meeting_platform.dto.MapDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
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