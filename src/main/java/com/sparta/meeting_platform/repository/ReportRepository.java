package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Boolean existsByUserIdAndBadMannerId(Long reporterId, Long badMannserId);
}
