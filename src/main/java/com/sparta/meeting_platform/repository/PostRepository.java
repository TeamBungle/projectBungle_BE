package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

    Post findByUserId(Long UserId);
    void deleteByUserId(Long userId);
    Post findByIdAndIsLetterFalse(Long postId);
}
