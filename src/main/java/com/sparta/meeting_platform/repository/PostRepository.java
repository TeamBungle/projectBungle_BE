package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {

    Post findByUserId(Long UserId);

    void deleteByUserId(Long userId);

    List<Post> findAllByUserId(Long userId);
}
