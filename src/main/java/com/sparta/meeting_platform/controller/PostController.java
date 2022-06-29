package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // 게시글 작성
    @PostMapping("/letter")
    public ResponseEntity<FinalResponseDto<?>> createPost(
            @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.createPost(userDetails.getUser().getId(), requestDto, files);
    }

    // 게시글 수정
    @PutMapping("/post/{postId}")
    public ResponseEntity<FinalResponseDto<?>> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.updatePost(postId, userDetails.getUser().getId(), requestDto, files);
    }


}
