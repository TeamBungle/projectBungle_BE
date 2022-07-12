package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByUsername(String username);
    ChatRoom findByUsername(String username);
}
