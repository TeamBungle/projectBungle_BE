package com.sparta.meeting_platform.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TempAndJoinCountSearchDto {
    private int joinCount;
    private int aveTemp;

    private List<String> joinPeopleUrl;
    private  List<String> joinPeopleNickName;
    private  List<String> joinPeopleIntro;

    public TempAndJoinCountSearchDto(int joinCount, int avgTemp) {
        this.joinCount = joinCount;
        this.aveTemp = avgTemp;
    }

    public TempAndJoinCountSearchDto(List<String> joinPeopleUrl, List<String> joinPeopleNickName,
                                     List<String> joinPeopleIntro, int avgTemp, int joinCount) {
        this.joinPeopleUrl = joinPeopleUrl;
        this.joinPeopleNickName = joinPeopleNickName;
        this.joinPeopleIntro = joinPeopleIntro;
        this.joinCount = joinCount;
        this.aveTemp = avgTemp;
    }
}
