package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.dto.FindChatMessageDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {
    List<FindChatMessageDto> findAllByRoomId(String roomId);

    ChatMessage findTop1ByRoomIdOrderByCreatedAtDesc(String roomId);

    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);

    void deleteByRoomId(String RoomId);
}
