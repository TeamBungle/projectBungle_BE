package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Report;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.ReportDto.ReportResponseDto;
import com.sparta.meeting_platform.exception.ReportApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.ReportRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // 유저 신고하기
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> setUserReport(Long reporterId, Long badMannerId, String history) {
        User user = userRepository.findById(reporterId).orElse(null);
        User reportedUser = userRepository.findById(badMannerId).orElse(null);

        if (user == null || reportedUser == null || reporterId.equals(badMannerId)) {
            throw new UserApiException("신고하기 실패");
        }
        Boolean isReport = reportRepository.existsByReporterIdAndBadMannerId(reporterId, badMannerId);

        if (isReport.equals(true)) {
            throw new ReportApiException("신고하기 실패");
        }
        Report report = new Report(reporterId, badMannerId, reportedUser.getNickName(), reportedUser.getProfileUrl(), history);
        reportRepository.save(report);
        reportedUser.setReport();

        return new ResponseEntity<>(new FinalResponseDto<>(true, "신고하기 성공"), HttpStatus.OK);
    }

    //신고내역 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getUserReport(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserApiException("신고 내역 조회 실패")
        );
        List<Report> reports = reportRepository.findAllByReporterIdOrderByIdDesc(user.getId());

        if (reports.size() < 1) {
            throw new ReportApiException("신고하기 실패");
        }
        List<ReportResponseDto> reportResponseDtos = new ArrayList<>();
        for (Report report : reports) {
            ReportResponseDto reportResponseDto = new ReportResponseDto(report);
            reportResponseDtos.add(reportResponseDto);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "신고 내역 조회 성공", reportResponseDtos, user.getIsOwner()), HttpStatus.OK);
    }
}
