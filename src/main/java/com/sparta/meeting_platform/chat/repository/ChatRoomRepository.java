package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
