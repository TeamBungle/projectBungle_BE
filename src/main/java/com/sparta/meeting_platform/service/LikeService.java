package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.Like;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //찜하기 및 취소하기
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> setLike(Long postId, Long userId) {
        // Like 테이블에서 사용자가 해당 post를 찜한 여부 확인
        Optional<Like> like = likeRepository.findByUser_IdAndPost_Id(userId, postId);
        if (like.isPresent()) {
            // 해당 행이 있으면 Isheart값 변경
            like.get().setIsLike();
        } else {
            // 처음 좋아요를 누르는 것이라면 생성하기위한 Post, User 가져오기기
            Post post = postRepository.findById(postId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            if (post == null || user == null) {
                return new ResponseEntity<>(new FinalResponseDto<>(false, "찜 할수 없습니다."), HttpStatus.OK);
            }
            Like likes = new Like(post, user);
            likeRepository.save(likes);
            return new ResponseEntity<>(new FinalResponseDto<>(true, "찜하기 성공"), HttpStatus.OK);
        }
        if (like.get().getIsLike() == true) {
            return new ResponseEntity<>(new FinalResponseDto<>(true, "찜하기 성공"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new FinalResponseDto<>(true, "찜하기 취소"), HttpStatus.OK);
        }
    }
}
