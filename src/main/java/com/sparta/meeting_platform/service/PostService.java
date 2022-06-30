package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.PostDto.PostRequestDto;
import com.sparta.meeting_platform.dto.PostDto.PostResponseDto;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.repository.mapping.PostMapping;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final S3Service s3Service;

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPosts(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }
        List<PostResponseDto> postList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        for (Post post : posts) {
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
            Boolean isLike;

            if(like == null){
                isLike = false;
            }else {
                isLike = like.getIsLike();
            }
            PostResponseDto postResponseDto = new PostResponseDto(post,isLike);
            postList.add(postResponseDto);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }

    //카테고리별 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByCategories(Long userId, List<String> categories) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }
        List<PostResponseDto> postList = new ArrayList<>();

        for (String category : categories) {
            List<Post> post = postRepository.findAllByCategories(category);

            for (Post posts : post) {
                PostResponseDto postResponseDto = new PostResponseDto(posts);
                postList.add(postResponseDto);
            }
        }

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }
    //태그별 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getPostsByTags(Long userId, List<String> tags) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }

        List<PostResponseDto> postList = new ArrayList<>();

        for (String tag : tags) {
            List<Post> post = postRepository.findAllByTags(tag);

            for (Post posts : post) {
                PostResponseDto postResponseDto = new PostResponseDto(posts);
                postList.add(postResponseDto);
            }
        }

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);

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

    //게시글 조회 (제목에 포함된 단어로)
    public ResponseEntity<FinalResponseDto<?>> getSearch(String keyword, Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 검색 실패"), HttpStatus.BAD_REQUEST);
        }
        List<PostResponseDto> postList = new ArrayList<>();
        List<Post> posts = postRepository.findAllByTitleContainsOrderByCreatedAtDesc(keyword);
        if(posts.size() < 1){
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글이 없습니다, 다른단어로 검색해주세요"), HttpStatus.BAD_REQUEST);
        }
        for (Post post : posts) {
            Like like = likeRepository.findByUser_IdAndPost_Id(userId, post.getId()).orElse(null);
            Boolean isLike;

            if (like == null) {
                isLike = false;
            } else {
                isLike = like.getIsLike();
            }
            PostResponseDto postResponseDto = new PostResponseDto(post, isLike);
            postList.add(postResponseDto);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 조회 성공", postList), HttpStatus.OK);
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<FinalResponseDto<?>>deletePost(Long postid, Long userId) {
        Post post = postRepository.findById(postid).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (!post.getUser().getId().equals(userId)) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "본인 게시글이 아닙니다."),HttpStatus.BAD_REQUEST);
        } else {
            postRepository.deleteById(postid);
            return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 삭제 성공"), HttpStatus.OK);
        }
    }

    // 게시글 등록
    public ResponseEntity<FinalResponseDto<?>> createPost(Long userId, PostRequestDto requestDto, List<MultipartFile> files) {
        User user = userRepository.findById(userId).orElse(null);

        if (user==null) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 개설 실패"), HttpStatus.BAD_REQUEST);
        }

        if(files.isEmpty()){
            requestDto.setPostUrls(null);  // 기본 이미지로 변경 필요
        } else {
            List<String> postUrls = new ArrayList<>();
            for(MultipartFile file : files){
                postUrls.add(s3Service.upload(file));
            }
            requestDto.setPostUrls(postUrls);
        }
        Post post = postRepository.save(new Post(user, requestDto));
        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 개설 성공"), HttpStatus.OK);
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> updatePost(Long postId, Long userId, PostRequestDto requestDto, List<MultipartFile> files) {
        Post post = postRepository.findByIdAndUserId(postId, userId).orElse(null);

        if(post == null){
            return new ResponseEntity<>(new FinalResponseDto<>(false, "게시글 수정 실패"), HttpStatus.BAD_REQUEST);
        }

        if(files.isEmpty()){
            requestDto.setPostUrls(null);  // 기본 이미지로 변경 필요
        } else {
            List<String> postUrls = new ArrayList<>();
            for(MultipartFile file : files){
                postUrls.add(s3Service.upload(file));
            }
            requestDto.setPostUrls(postUrls);
        }
        // DB 업데이트
        post.update(requestDto);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "게시글 수정 성공"), HttpStatus.OK);

    }

    // 좋아요한 게시글 전체 조회
    public ResponseEntity<FinalResponseDto<?>> getLikedPosts(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "좋아요한 게시글 조회 실패"), HttpStatus.BAD_REQUEST);
        }
        List<PostMapping> post = likeRepository.findAllByUserIdAndIsLikeTrue(userId);

        List<PostResponseDto> postList = new ArrayList<>();

        for (PostMapping posts : post) {
            PostResponseDto postResponseDto = new PostResponseDto(posts.getPost());
            postList.add(postResponseDto);
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "좋아요한 게시글 조회 성공", postList), HttpStatus.OK);
    }


}

