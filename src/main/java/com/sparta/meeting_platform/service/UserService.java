package com.sparta.meeting_platform.service;


import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.EmailToken;
import com.sparta.meeting_platform.domain.ResignUser;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.domain.UserRoleEnum;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.dto.UserDto.*;
import com.sparta.meeting_platform.exception.EmailApiException;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.*;
import com.sparta.meeting_platform.security.JwtTokenProvider;

import com.sparta.meeting_platform.util.FileExtFilter;
import io.jsonwebtoken.Jwts;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.security.redis.RedisService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private final EmailConfirmTokenRepository emailConfirmTokenRepository;
    private final RedisService redisService;

    private final FileExtFilter fileExtFilter;

    private final InvitedUsersRepository invitedUsersRepository;
//    private final RedisService redisService;

    // 아이디(이메일) 중복 확인
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> duplicateUsername(DuplicateRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new UserApiException("중복된 이메일이 존재합니다.");
        }
        return new ResponseEntity<>(new FinalResponseDto<>(true, "사용 가능한 이메일입니다."), HttpStatus.OK);
    }

    // 회원 가입
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> signup(SignupRequestDto requestDto) {

        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new UserApiException("비밀번호가 일치하지 않습니다.");
        }
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new UserApiException("이메일 중복체크는 필수입니다.");
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
    public ResponseEntity<FinalResponseDto<?>> login(LoginRequestDto requestDto){
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new UserApiException("해당 유저를 찾을 수 없습니다.")
        );
        // 유저 권한 확인
        userRoleCheckService.userRoleCheck(user);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserApiException("비밀번호를 확인해 주세요");
        }

        //
        accessAndRefreshTokenProcess(user.getUsername());

        return new ResponseEntity<>(new FinalResponseDto<>
                        (true, "로그인 성공!!", user.getNickName(),user.getMannerTemp(),user.getId()),HttpStatus.OK);
    }

    // 이메일 인증 토큰 확인
    @Transactional
    public String confirmEmail(String token){
        EmailToken emailToken = emailConfirmTokenRepository.findById(token).orElseThrow(
                ()-> new EmailApiException("토큰 정보가 없습니다.")
        );
        if(emailToken.getExpirationDate().isAfter(LocalDateTime.now())){
            Optional<User> user = userRepository.findByUsername(emailToken.getUserEmail());
            emailToken.useToken();    // 토큰 만료

            if (!user.isPresent()) {
                throw new UserApiException("잘못된 토큰값");
            }
            user.get().setRole(UserRoleEnum.USER);
        } else {
            // 새 인증 token 전송
            emailConfirmTokenService.createEmailConfirmationToken(emailToken.getUserEmail());
            // 만료된 token 삭제
            emailConfirmTokenService.deleteExpiredDateToken(emailToken.getId());
            return "기존 인증 코드가 만료되어 이메일 재발송 하였습니다.";
        }
        return "";
    }

    // 프로필 수정
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> setProfile(Long userId, ProfileRequestDto requestDto, MultipartFile file) {
        Optional<User> user = userRepository.findById(userId);

        if(!fileExtFilter.badFileExt(file)){
            throw new PostApiException("이미지가 아닙니다.");
        }
        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "프로필 설정 실패"), HttpStatus.OK);
        }
        String profileUrl;

        if(file!=null){
            profileUrl = s3Service.upload(file);
        } else {
            profileUrl = user.get().getProfileUrl();
        }
        user.get().updateProfile(requestDto, profileUrl);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "프로필 조회 성공",new ProfileResponseDto(user.get())), HttpStatus.OK);
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            return new ResponseEntity<>(new FinalResponseDto<>(false, "회원 탈퇴 실패"), HttpStatus.OK);
        }
        // 탈퇴 회원 테이블에 저장(이메일, 시간, 매너온도)
        ResignUser resignUser = new ResignUser(user.get());
        resignUserRepository.save(resignUser);

        // 영속 되는 데이터 삭제 ( 추후 cascade 설정 필요 )
        invitedUsersRepository.deleteByUserId(userId);
        likeRepository.deleteByUserId(userId);
        opinionRepository.deleteByUserId(userId);
        postRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

        return new ResponseEntity<>(new FinalResponseDto<>(true, "회원 탈퇴 성공"), HttpStatus.OK);
    }

    //페이지 이동
    @Transactional(readOnly = true)
    public ResponseEntity<FinalResponseDto<?>> getProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->  new UserApiException("프로필 조회 실패")
        );

        return new ResponseEntity<>(new FinalResponseDto<>(true, "프로필 조회 성공",new ProfileResponseDto(user)), HttpStatus.OK);

    }

    // 만료된 access token 재 발급
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> refreshToken(String accessToken, String refreshToken) {
        // accessToken 만료 기간 확인
        if(jwtTokenProvider.validateToken(accessToken)){
            throw new UserApiException("토큰 기간이 만료되지 않아서 갱신되지 않습니다");
        }

        // RefreshToken 유효성 검사
        String token = refreshToken.replace("Bearer ", "");
        if(!jwtTokenProvider.validateToken(token)){
            throw new UserApiException("refresh token 기간이 만료 되었습니다.");
        }

        // Redis에서 refreshToken 유저 정보 꺼내기
        String username = redisService.getValues(token);
        if(username == null){
            throw new UserApiException("토큰 정보가 없습니다.");
        }

        // 토큰 재발행
        accessAndRefreshTokenProcess(username);
        // 기존 토큰 삭제
        redisService.deleteValues(token);

        return new ResponseEntity<>(new FinalResponseDto<>
                (true, "access token 갱신 완료"), HttpStatus.OK);
    }

    public void accessAndRefreshTokenProcess(String username){
        String refreshToken = jwtTokenProvider.createRefreshToken();
        redisService.setValues(refreshToken, username);
        redisService.setExpire(refreshToken, 7*24*60*60*1000L, TimeUnit.MILLISECONDS);
        jwtTokenProvider.createToken(username);
    }


}
