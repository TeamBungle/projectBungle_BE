package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUser_IdAndPost_Id(Long postId, Long userId);

    List<PostMapping> findAllByUserIdAndIsLikeTrue(Long userId);

//    List<Like> findAllByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserId(Long userId);
}
