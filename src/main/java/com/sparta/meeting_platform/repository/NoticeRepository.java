package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice,Long> {

    List<Notice> findAllByOrderByIdDesc();
}
