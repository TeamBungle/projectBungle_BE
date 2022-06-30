package com.sparta.meeting_platform.repository;

import com.sparta.meeting_platform.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    Optional<Post> findByIdAndUserId(Long postId, Long userId);

    List<Post> findAllByCategories(String categories);

    List<Post> findAllByTags(String tags);

    List<Post> findAllByTitleContainsOrderByCreatedAtDesc(String keyword);
}
