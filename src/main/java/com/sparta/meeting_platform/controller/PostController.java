package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    private Long getUserId(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return user.getId();
    }

    //게시긇 전체 조회
    @GetMapping("")
    public ResponseEntity<FinalResponseDto<?>> getPosts(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = getUserId(userDetails);
        return postService.getPosts(userId);
    }

    //게시글 상세 조회
    @GetMapping("/{postid}")
    public ResponseEntity<FinalResponseDto<?>> getPostDetails(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = getUserId(userDetails);
        return postService.getPostsDetails(postid, userId);
    }

    //게시글 삭제
    @DeleteMapping(value = {"/{postid}/letter", "/{postid}/video"})
    public ResponseEntity<FinalResponseDto<?>> deletePost(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = getUserId(userDetails);
        return postService.deletePost(postid, userId);
    }
}
