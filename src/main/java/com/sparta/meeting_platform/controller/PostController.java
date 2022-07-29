package com.sparta.meeting_platform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.LikeService;
import com.sparta.meeting_platform.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;

@Slf4j
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

    //게시글 전체 조회
    @GetMapping("")
    public ResponseEntity<FinalResponseDto<?>> getPosts(@RequestParam(value = "latitude") Double latitude,
                                                        @RequestParam(value = "longitude") Double longitude,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws org.locationtech.jts.io.ParseException {
        Long userId = userDetails.getUser().getId();
        return postService.getPosts(userId,latitude,longitude);
    }

    // 카테고리별 게시글 조회
    @GetMapping("/categories")
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(
            @RequestParam(value = "categories",required = false, defaultValue = "") List<String> categories,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws org.locationtech.jts.io.ParseException {
        Long userId = getUserId(userDetails);
        return postService.getPostsByCategories(userId,categories,latitude,longitude);
    }

    //태그별 게시글 조회
    @GetMapping("/tags")
    public ResponseEntity<FinalResponseDto<?>> getPostsByTags(
            @RequestParam(value = "tags",required = false,defaultValue = "") List<String> tags,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws org.locationtech.jts.io.ParseException {
        Long userId = getUserId(userDetails);
        return postService.getPostsByTags(userId,tags,latitude,longitude);
    }

    //태그별 게시글 무한 스크롤
    @GetMapping("/tags/{lastId}")
    public ResponseEntity<FinalResponseDto<?>> getTagsInfiniteScroll(
            @PathVariable Long lastId,
            @RequestParam(value = "tags",required = false, defaultValue = "") List<String> tags,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws org.locationtech.jts.io.ParseException {
        Long userId = getUserId(userDetails);
        return postService.gettagsInfiniteScroll(lastId, tags, latitude, longitude, userId, size);
    }

    //게시글 더보기 조회
    @GetMapping("/more")
    public ResponseEntity<FinalResponseDto<?>> morePostList(@RequestParam(value = "latitude") Double latitude,
                                                            @RequestParam(value = "longitude") Double longitude,
                                                            @RequestParam(value = "status") String status,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
        Long userId = getUserId(userDetails);
        return postService.morePostList(userId,status,latitude,longitude);
    }

    // 게시글 더보기 무한스크롤
    @GetMapping("/more/{lastId}")
    public ResponseEntity<FinalResponseDto<?>> morePostListInfiniteScroll(
            @PathVariable Long lastId,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
        Long userId = getUserId(userDetails);
        return postService.morePostListInfiniteScroll(lastId, userId,status,latitude,longitude,size);
    }

    //게시글 상세 조회
    @GetMapping("/{postid}")
    public ResponseEntity<FinalResponseDto<?>> getPostDetails(
            @PathVariable Long postid,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.getPostsDetails(postid, userId);
    }


    // 게시글 작성
    @PostMapping("")
    public ResponseEntity<FinalResponseDto<?>> createPost(
            @Valid @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg",required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {

        return postService.createPost(getUserId(userDetails), requestDto, files);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<FinalResponseDto<?>> updatePost(
            @PathVariable Long postId,
            @RequestPart(value = "postDto") PostRequestDto requestDto,
            @RequestPart(value = "postImg",required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {

        return postService.updatePost(postId, getUserId(userDetails), requestDto, files);
    }

    //게시글 수정 페이지 이동
    @GetMapping("/mypost")
    public ResponseEntity<FinalResponseDto<?>> getMyPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = getUserId(userDetails);
        return postService.getMyPost(userId);
    }

    //게시글 삭제
    @DeleteMapping("/{postid}")
    public ResponseEntity<FinalResponseDto<?>> deletePost(
            @PathVariable Long postid,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws JsonProcessingException {
        Long userId = getUserId(userDetails);
        return postService.deletePost(postid, userId);
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
//                                                         @RequestParam(value = "latitude") Double latitude,
//                                                         @RequestParam(value = "longitude") Double longitude,
//                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {
//        Long userId = getUserId(userDetails);
//        return postService.getSearch(keyword,userId,longitude,latitude);
//    }

}
