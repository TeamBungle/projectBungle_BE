package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomId(String roomId);
    ChatMessage findTop1ByRoomIdOrderByCreatedAtDesc(String roomId);

    void deleteByRoomId(String RoomId);
}
