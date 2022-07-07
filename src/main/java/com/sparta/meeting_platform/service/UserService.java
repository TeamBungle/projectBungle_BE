package com.sparta.meeting_platform.service;


import com.sparta.meeting_platform.domain.EmailToken;
import com.sparta.meeting_platform.domain.ResignUser;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.domain.UserRoleEnum;
import com.sparta.meeting_platform.dto.user.DuplicateRequestDto;
import com.sparta.meeting_platform.dto.user.LoginRequestDto;
import com.sparta.meeting_platform.dto.user.ProfileRequestDto;
import com.sparta.meeting_platform.dto.user.SignupRequestDto;
import com.sparta.meeting_platform.repository.*;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3Service;
    private final EmailConfirmTokenService emailConfirmTokenService;
    private final ResignUserRepository resignUserRepository;
    private final LikeRepository likeRepository;
    private final OpinionRepository opinionRepository;
    private final PostRepository postRepository;
    private final UserRoleCheckService userRoleCheckService;

    // 아이디(이메일) 중복 확인
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> duplicateUsername(DuplicateRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "사용 가능한 이메일입니다."), HttpStatus.OK);
    }

    // 회원 가입
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> signup(SignupRequestDto requestDto) throws MessagingException {

        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이메일 중복체크는 필수입니다.");
        }

        // 재가입 방지
        int mannerTemp = userRoleCheckService.userResignCheck(requestDto.getUsername());

        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(new User(requestDto, mannerTemp));

        // 인증 메일 전송
        emailConfirmTokenService.createEmailConfirmationToken(requestDto.getUsername());

        return new ResponseEntity<>(new FinalResponseDto<>(true, "회원가입 성공"), HttpStatus.OK);
    }

    // 로그인
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
        // 유저 권한 확인
        userRoleCheckService.userRoleCheck(user);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 확인해 주세요");
        }
        jwtTokenProvider.createToken(requestDto.getUsername());

        return new ResponseEntity<>(new FinalResponseDto<>
                        (true, "로그인 성공!!", user.getNickName(),user.getMannerTemp()), HttpStatus.OK);
    }

    // 이메일 인증 토큰 확인
    @Transactional
    public void confirmEmail(String token) {
        EmailToken emailToken = emailConfirmTokenService.findByIdAndExpirationDateAfterAndExpired(token);
        Optional<User> user = userRepository.findByUsername(emailToken.getUserEmail());
        emailToken.useToken();    // 토큰 만료

        if (!user.isPresent()) {
            throw new NullPointerException("잘못된 토큰값");
        }
        user.get().setRole(UserRoleEnum.USER);
    }

    // 프로필 수정
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> setProfile(Long userId, ProfileRequestDto requestDto, MultipartFile file) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "프로필 설정 실패"), HttpStatus.BAD_REQUEST);
        }
        String profileUrl;

        if(!file.isEmpty()){
            profileUrl = s3Service.upload(file);
        } else {
            profileUrl = user.get().getProfileUrl();
        }
        user.get().updateProfile(requestDto, profileUrl);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "프로필 설정 성공"), HttpStatus.OK);
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "회원 탈퇴 실패"), HttpStatus.BAD_REQUEST);
        }
        // 탈퇴 회원 테이블에 저장(이메일, 시간, 매너온도)
        ResignUser resignUser = new ResignUser(user.get());
        resignUserRepository.save(resignUser);

        // 영속 되는 데이터 삭제 ( 추후 cascade 설정 필요 )
        likeRepository.deleteByUserId(userId);
        opinionRepository.deleteByUserId(userId);
        postRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "회원 탈퇴 성공"), HttpStatus.OK);
    }

}
