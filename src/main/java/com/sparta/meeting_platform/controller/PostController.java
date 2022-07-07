package com.sparta.meeting_platform.controller;

import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostTestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.LikeService;
import com.sparta.meeting_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;


    private Long getUserId(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return user.getId();
    }

    //게시긇 전체 조회
    @GetMapping("")
    public ResponseEntity<FinalResponseDto<?>> getPosts(@RequestParam(value = "latitude") Double latitude,
                                                        @RequestParam(value = "longitude") Double longitude,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
        Long userId = getUserId(userDetails);
        return postService.getPosts(userId,latitude,longitude);
    }

    // 카테고리별 게시글 조회
    @GetMapping("/categories")
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(
            @RequestParam(value = "categories",required = false, defaultValue = "") List<String> categories,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.getPostsByCategories(userId,categories);
    }

    //태그별 게시글 조회
    @GetMapping("/tags")
    public ResponseEntity<FinalResponseDto<?>> getPostsByTags(
            @RequestParam(value = "tags",required = false,defaultValue = "") List<String> tags,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.getPostsByTags(userId,tags);
    }

    //게시글 상세 조회
    @GetMapping("/{postid}")
    public ResponseEntity<FinalResponseDto<?>> getPostDetails(
            @PathVariable Long postid,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.getPostsDetails(postid, userId);
    }

    //게시글 삭제
    @DeleteMapping("/{postid}")
    public ResponseEntity<FinalResponseDto<?>> deletePost(
            @PathVariable Long postid,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.deletePost(postid, userId);
    }

    // 게시글 작성
    @PostMapping("")
    public ResponseEntity<FinalResponseDto<?>> createPost(
            @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {

        return postService.createPost(getUserId(userDetails), requestDto, files);
    }

    // 게시글 수정
    @PutMapping("/post/{postId}")
    public ResponseEntity<FinalResponseDto<?>> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {

        return postService.updatePost(postId, getUserId(userDetails), requestDto, files);
    }

    // 찜하기
    @PostMapping("/like/{postId}")
    public ResponseEntity<FinalResponseDto<?>> heartClick(
            @PathVariable Long postId ,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return likeService.setLike(postId, getUserId(userDetails));
    }

    // 찜한 게시글 전체 조회
    @GetMapping("/like")
    public ResponseEntity<FinalResponseDto<?>> getLiedPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getLikedPosts(getUserId(userDetails));
    }

    //나의 번개 페이지 조회
    @GetMapping("/mypage")
    public ResponseEntity<FinalResponseDto<?>> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.getMyPage(userDetails);
    }
    //내 벙글 조회
    @GetMapping("/mypage/post")
    public ResponseEntity<FinalResponseDto<?>> getMyPagePost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getMyPagePost(userDetails);
    }



//    //게시글 검색(제목에포함된단어로)
//    @GetMapping("/search")
//    public ResponseEntity<FinalResponseDto<?>> getSearch(@RequestParam(value = "keyword") String keyword,
//                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
//        Long userId = getUserId(userDetails);
//        return postService.getSearch(keyword,userId);
//    }

}
