package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.LeftChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeftChatRoomJpaRepository extends JpaRepository<LeftChatRoom, Long> {
}
