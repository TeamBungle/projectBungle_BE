package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Report;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.exception.ReportApiException;
import com.sparta.meeting_platform.repository.ReportRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public ResponseEntity<FinalResponseDto<?>> setUserReport(Long reporterId, Long badMannserId) {
        User user = userRepository.findById(reporterId).orElse(null);
        User reportedUser = userRepository.findById(badMannserId).orElse(null);

        if (user==null || reportedUser==null || reporterId.equals(badMannserId)) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "신고하기 실패"), HttpStatus.OK);
        }
        Boolean isReport = reportRepository.existsByReporterIdAndBadMannerId(reporterId, badMannserId);

        if(isReport.equals(true)){
            return new ResponseEntity<>(new FinalResponseDto<>(false, "신고하기 실패"), HttpStatus.OK);
        }
        Report report = new Report(reporterId, badMannserId);
        reportRepository.save(report);
        reportedUser.setReport();
        return new ResponseEntity<>(new FinalResponseDto<>(true, "신고하기 성공"), HttpStatus.OK);

    }
    @Transactional(readOnly = true)
    //신고내역 조회
    public ResponseEntity<FinalResponseDto<?>> getUserReport(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ReportApiException("신고 내역 조회 실패")
        );
        List<Report> report = reportRepository.findAllByReporterId(user.getId());

        if (report.size() < 1){
            return new ResponseEntity<>(new FinalResponseDto<>(false,"신고내역이 존재하지 않습니다"),HttpStatus.OK);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true,"신고 내역 조회 성공", report),HttpStatus.OK);
    }
}
