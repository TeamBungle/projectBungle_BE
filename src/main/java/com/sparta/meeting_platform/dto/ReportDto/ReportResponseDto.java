package com.sparta.meeting_platform.dto.ReportDto;

import com.sparta.meeting_platform.domain.Report;
import lombok.Getter;

@Getter
public class ReportResponseDto {

    private final String nickName;

    private final String profileUrl;

    private final String history;

    public ReportResponseDto(Report report){
        this.nickName = report.getBadMannerNickName();
        this.profileUrl = report.getBadMannerUrl();
        this.history= report.getHistory();
    }

}
