package com.sparta.meeting_platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.UserDto.LoginRequestDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.QrcodeApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class QrcodeCheckService {
    private final SocialKakaoService socialKakaoService;
    private final SocialNaverService socialNaverService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InvitedUsersRepository invitedUsersRepository;
    private final UserRoleCheckService userRoleCheckService;
    private final SocialGoogleService socialGoogleService;
    private final UserService userService;

    //qr코드 일반로그인 유저 인증 처리
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> qrcodeUserCheck(Long postId, LoginRequestDto loginRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        userRoleCheckService.userRoleCheck(user);
        userService.accessAndRefreshTokenProcess(user.getUsername());
        qrcodeConfirm(post, user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!", user), HttpStatus.OK);
    }

    //qr코드 인증처리
    public void qrcodeConfirm(Post post, User user) {
        try {
            InvitedUsers invitedUsers = invitedUsersRepository.findByUserIdAndPostId(user.getId(), post.getId());

            if (!(invitedUsers.getUser().equals(user) && invitedUsers.getPostId().equals(post.getId()))) {
                throw new QrcodeApiException("현 모임에 참여한 유저가 아닙니다.");
            } else if (invitedUsers.getQrCheck()) {
                throw new QrcodeApiException("이미 QR 인증이 되셨습니다.");
            } else if (post.getUser().equals(user)) {
                throw new QrcodeApiException("본인 게시글의 QR코드는 적용되지 않습니다.");
            } else {
                user.updateMannerTempAndBungCount();
                invitedUsers.updateQrCheck();
            }
        } catch (NullPointerException e) {
            throw new QrcodeApiException("현 모임에 참여한 유저가 아닙니다.");
        }
    }

    //qr코드 구글로그인 유저 인증처리
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> googleLogin(String authCode, Long postId) throws JsonProcessingException {
        ResponseEntity<FinalResponseDto<?>> finalResponseDtoResponseEntity
                = socialGoogleService.googleLogin(authCode);
        Long userId = finalResponseDtoResponseEntity.getBody().getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        qrcodeConfirm(post, user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!", user), HttpStatus.OK);
    }

    //qr코드 카카오로그인 유저 인증처리
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(String code, Long postId) throws JsonProcessingException {
        ResponseEntity<FinalResponseDto<?>> finalResponseDtoResponseEntity
                = socialKakaoService.kakaoLogin(code);
        Long userId = finalResponseDtoResponseEntity.getBody().getUserId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));

        qrcodeConfirm(post, user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!", user), HttpStatus.OK);
    }

    //qr코드 네이버로그인 유저 인증처리
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> naverLogin(String code, String state, Long postId) {
        ResponseEntity<FinalResponseDto<?>> finalResponseDtoResponseEntity
                = socialNaverService.naverLogin(code, state);
        Long userId = finalResponseDtoResponseEntity.getBody().getUserId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));

        qrcodeConfirm(post, user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!", user), HttpStatus.OK);
    }
}
