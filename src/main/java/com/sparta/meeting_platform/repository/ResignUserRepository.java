package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.ResignUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResignUserRepository extends JpaRepository<ResignUser, Long> {
    Optional<ResignUser> findByUsername(String username);
}
