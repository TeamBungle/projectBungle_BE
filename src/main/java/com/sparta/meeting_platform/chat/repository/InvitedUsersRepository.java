package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitedUsersRepository extends JpaRepository<InvitedUsers, Long> {
    void deleteByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndPostId(Long user_id, Long postId);
    List<InvitedUsers> findAllByUserId(Long userId);
    void deleteAllByPostId(Long postId);
    void deleteByUserId(Long userId);
    void deleteByUser(User user);
    List<InvitedUsers> findAllByPostId(Long postId);

    InvitedUsers findByUserIdAndPostId(Long id, Long id1);

    Optional<InvitedUsers> findByUserId(Long userId);

    List<InvitedUsers> findAllByUserIdAndReadCheck(Long userId, Boolean readCheck);
}
