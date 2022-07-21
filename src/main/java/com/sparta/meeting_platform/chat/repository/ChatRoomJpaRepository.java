package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByUsername(String username);

    ChatRoom findByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
