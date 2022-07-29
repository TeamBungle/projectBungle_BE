package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.ResignChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignChatRoomJpaRepository extends JpaRepository <ResignChatRoom, Long> {
    ResignChatRoom findByRoomId(String roomId);
}
