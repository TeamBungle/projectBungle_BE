package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
