package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.QrcodeApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QrcodeCheckService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InvitedUsersRepository invitedUsersRepository;

    @Transactional
    public void qrcodeUserCheck(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));

        if(!invitedUsersRepository.existsByUserIdAndRoomId(user.getId(),post.getId().toString())){
            throw new QrcodeApiException("현 모임에 참여한 유저가 아닙니다.");
        }else if(post.getUser().equals(user)){
            throw new QrcodeApiException("본인 게시글의 QR코드는 적용되지 않습니다.");
        }else{
            user.updateMannerTemp();
        }
    }
}
