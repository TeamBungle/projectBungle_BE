package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUser_IdAndPost_Id(Long postId, Long userId);
    List<PostMapping> findAllByUserIdAndIsLikeTrueOrderByPost_Id(Long userId);
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);

}
