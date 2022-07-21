package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String email);
    Optional<User> findByKakaoId(Long kakaoId);
    Boolean existsByUsername(String username);
    Optional<User> findByNaverId(String naverId);
}
