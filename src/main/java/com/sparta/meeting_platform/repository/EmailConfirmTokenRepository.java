package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailConfirmTokenRepository extends JpaRepository<EmailToken, String> {

    EmailToken findByUserEmail(String username);
}
