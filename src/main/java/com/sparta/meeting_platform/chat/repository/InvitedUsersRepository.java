package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitedUsersRepository extends JpaRepository<InvitedUsers, Long> {
List<InvitedUsers> findAllByUserId(Long userId);

    List<InvitedUsers> findAllByRoomId(String roomId);

    boolean existsByUserId(Long id);
}
