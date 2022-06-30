package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Report;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.repository.ReportRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return new ResponseEntity<>(new FinalResponseDto<>(false, "신고하기 실패"), HttpStatus.BAD_REQUEST);
        }
        Boolean isReport = reportRepository.existsByUserIdAndBadMannerId(reporterId, badMannserId);

        if(isReport.equals(true)){
            return new ResponseEntity<>(new FinalResponseDto<>(false, "신고하기 실패"), HttpStatus.BAD_REQUEST);
        }
        Report report = new Report(user, badMannserId);
        reportRepository.save(report);
        reportedUser.setReport();
        return new ResponseEntity<>(new FinalResponseDto<>(true, "신고하기 성공"), HttpStatus.OK);

    }

}
