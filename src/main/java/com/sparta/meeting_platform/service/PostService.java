package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPosts(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }
        List<PostResponseDto> postList = new ArrayList<>();
        List<Post> post = postRepository.findAllByOrderByCreatedAtDesc();

        for (Post posts : post) {
            PostResponseDto postResponseDto = new PostResponseDto(posts);
            postList.add(postResponseDto);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공"), HttpStatus.OK);
    }

    //게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>>getPostsDetails(Long postId, Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시물 입니다.")
        );
        PostResponseDto postResponseDto = new PostResponseDto(post);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공",postResponseDto), HttpStatus.OK);
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<FinalResponseDto<?>>deletePost(Long postid, Long userId) {
        Post post = postRepository.findById(postid).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (post.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "본인 게시글이 아닙니다."),HttpStatus.BAD_REQUEST);
        } else {
            postRepository.deleteById(postid);
            return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 삭제 성공"), HttpStatus.OK);
        }
    }

}

