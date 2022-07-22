package com.sparta.meeting_platform.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.config.GoogleConfig;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginRequestDto;
import com.sparta.meeting_platform.dto.GoogleDto.GoogleLoginResponseDto;
import com.sparta.meeting_platform.dto.KakaoDto.KakaoUserInfoDto;
import com.sparta.meeting_platform.dto.NaverDto.NaverUserDto;

import com.sparta.meeting_platform.dto.UserDto.LoginRequestDto;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.QrcodeApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class QrcodeCheckService {
    private final SocialKakaoService socialKakaoService;
    private final SocialNaverService socialNaverService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InvitedUsersRepository invitedUsersRepository;
    private final GoogleConfig googleConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRoleCheckService userRoleCheckService;
    private final SocialGoogleService socialGoogleService;

    @Transactional
    public ResponseEntity<FinalResponseDto<?>> qrcodeUserCheck(Long postId, LoginRequestDto loginRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        userRoleCheckService.userRoleCheck(user);
        jwtTokenProvider.createToken(user.getUsername());
        qrcodeConfirm(post,user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!"), HttpStatus.OK);
    }

    public void qrcodeConfirm (Post post ,User user){
        try {
            InvitedUsers invitedUsers = invitedUsersRepository.findByUserIdAndPostId(user.getId(),post.getId());

            if(!(invitedUsers.getUser().equals(user) && invitedUsers.getPostId().equals(post.getId()))){
                throw new QrcodeApiException("현 모임에 참여한 유저가 아닙니다.");
            }else if(invitedUsers.getQrCheck()){
                throw new QrcodeApiException("이미 QR 인증이 되셨습니다.");
            } else if(post.getUser().equals(user)){
                throw new QrcodeApiException("본인 게시글의 QR코드는 적용되지 않습니다.");
            }else{
                user.updateMannerTemp();
                invitedUsers.updateQrCheck();

            }
        }catch (QrcodeApiException e){
            throw new QrcodeApiException("현 모임에 참여한 유저가 아닙니다.");
        }
    }

    @Transactional
    public ResponseEntity<FinalResponseDto<?>> googleLogin(String authCode, HttpServletResponse httpServletResponse, Long postId) throws JsonProcessingException {
        ResponseEntity<FinalResponseDto<?>> finalResponseDtoResponseEntity
                = socialGoogleService.googleLogin(authCode,httpServletResponse);
        Long userId = finalResponseDtoResponseEntity.getBody().getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));
        qrcodeConfirm(post,user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<FinalResponseDto<?>> kakaoLogin(String code, HttpServletResponse response, Long postId) throws JsonProcessingException {
        String accessToken = socialKakaoService.getAccessToken(code);
        KakaoUserInfoDto kakaoUserInfoDto = socialKakaoService.getKakaoUserInfo(accessToken);
        userRoleCheckService.userResignCheck(kakaoUserInfoDto.getEmail());

        User user = userRepository.findByUsername(kakaoUserInfoDto.getEmail()).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostApiException("존재하지 않는 게시물 입니다."));

        Authentication authentication = socialKakaoService.forceLoginKakaoUser(user);
        socialKakaoService.kakaoUsersAuthorizationInput(authentication, response);
        qrcodeConfirm(post,user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!"), HttpStatus.OK);
    }


    public ResponseEntity<FinalResponseDto<?>> naverLogin(String code, String state, HttpServletResponse response, Long postId) throws IOException {
            // 네이버에서 가져온 유저정보 + 임의 비밀번호 생성
            NaverUserDto naverUser = socialNaverService.getNaverUserInfo(code, state);
            // 재가입 방지
            userRoleCheckService.userResignCheck(naverUser.getEmail().substring(1,naverUser.getEmail().length()-1));
            // 네이버 ID로 유저 정보 DB 에서 조회
            User user = userRepository.findByUsername(naverUser.getEmail().substring(1,naverUser.getEmail().length()-1)).orElse(null);
            // User 권한 확인
            userRoleCheckService.userRoleCheck(user);
            // 강제 로그인
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateJwtToken(userDetails);
            response.addHeader("Authorization", "Bearer" + " " + token);

            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new PostApiException("존재하지 않는 게시물 입니다."));

        qrcodeConfirm(post,user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "만남 성공!!"), HttpStatus.OK);
    }
}
