package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion,Long> {
}
