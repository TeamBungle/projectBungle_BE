package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ResignChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignChatMessageJpaRepository extends JpaRepository<ResignChatMessage, Long> {
}
