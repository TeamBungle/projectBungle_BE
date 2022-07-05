package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
}
