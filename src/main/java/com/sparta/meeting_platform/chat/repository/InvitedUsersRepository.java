package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitedUsersRepository extends JpaRepository<InvitedUsers, Long> {
    List<InvitedUsers> findAllByRoomId(String roomId);
    void deleteByUserIdAndRoomId(Long userId, String roomId);
    boolean existsByUserIdAndRoomId(Long user_id, String roomId);
}