package com.sparta.meeting_platform.chat.videoChat;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.exception.ChatApiException;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 세션정보와, DB정보를 Redis에서 관리해야되는지 고민 필요!
 */

@Service
@Slf4j
public class SessionService {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private InvitedUsersRepository invitedUsersRepository;

    // session이름, openvidu session 객체
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    // name, token
    // inner Map : token, role
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();
    // openvidu server url
    private final String OPENVIDU_URL;
    // openvidu server 비번
    private final String SECRET;
    // OpenVidu entrypoint : OpenVidu(String hostname, String secret)
    private OpenVidu openVidu;

    // 생성자
    public SessionService(
            @Value("${openvidu.secret}") String secret,
            @Value("${openvidu.url}") String openviduUrl,
            UserRepository userRepository,
            PostRepository postRepository,
            InvitedUsersRepository invitedUsersRepository) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.invitedUsersRepository = invitedUsersRepository;
    }

    // 화상 채팅 방 입장
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> enterRoom(Long postId, Long userId){

        User user = userCheck(userId);
        Post post = postCheck(postId);
        if(post == null)throw new ChatApiException("해당 게시글을 찾을 수 없습니다.");

        // 채팅방 참가자 테이블 관리
        if(post.getPersonnel() <= invitedUsersRepository.countByPostId(postId))throw new ChatApiException("정원이 초과하여 입장할 수 없습니다.");
        InvitedUsers invitedUsers = new InvitedUsers(postId, user);
        invitedUsersRepository.save(invitedUsers);

        // 화상 채팅방 id
        String roomId = String.valueOf(postId);
        // 권한 설정
        OpenViduRole role;
        if(userId.equals(post.getUser().getId())){
            role = OpenViduRole.PUBLISHER;
        }else{
            role = OpenViduRole.SUBSCRIBER;
        }
        log.info("role : {}", role);
        //OpenViduRole.SUBSCRIBER 다른사람이 개설한 방 참여만 가능
        //OpenViduRole.PUBLISHER  subscriber권한 + 방 개설 가능
        //OpenViduRole.MODERATOR  subscriber + publisher + 다른사람이 개설한 방에 대해 권한 부여(운영자)

        String token = getToken(user, role, roomId);

        VideoChatResponseDto videoChatResponseDto = new VideoChatResponseDto(token, user);
        return new ResponseEntity<>(new FinalResponseDto<>(true, "화상 채팅 입장 성공", videoChatResponseDto), HttpStatus.OK);

    }



    // 화상 채팅 방 퇴장
    @Transactional
    public ResponseEntity<FinalResponseDto<?>> leaveRoom(VideoChatLeaveRequestDto requestDto, Long userId) throws ParseException {
        User user = userCheck(userId);
        Post post = postCheck(requestDto.getPostId());
        if(post == null)throw new ChatApiException("해당 게시글을 찾을 수 없습니다.");

        // 채팅방 참가자 테이블 관리
        invitedUsersRepository.deleteByUserIdAndPostId(userId, requestDto.getPostId());

        // 화상 채팅방 id
        String roomId = String.valueOf(requestDto.getPostId());

        // session 이 존재 한다면.
        if (this.mapSessions.get(roomId) != null && this.mapSessionNamesTokens.get(roomId) != null) {
            // token 이 있다면 user 정보 삭제
            if (this.mapSessionNamesTokens.get(roomId).remove(requestDto.getToken()) != null) {
                if (this.mapSessionNamesTokens.get(roomId).isEmpty()) {
                    // 남은 유저가 없다면 session 정보 삭제
                    this.mapSessions.remove(roomId);
                }
                return new ResponseEntity<>(new FinalResponseDto<>(true, "화상 채팅 퇴장 성공"), HttpStatus.OK);
            } else {
                // TOKEN 에러
                throw new ChatApiException("화상 채팅방 나가기 시도 중 서버 오류");
            }
        } else {
            throw new ChatApiException("화상 채팅방 나가기 시도 중 서버 오류 - session 이 존재 하지 않음");
        }

    }

    // 유저가 화상 채팅에 연결할 때 다른 사용자에게 전달할 데이터(Token 생성)
    private String getToken(User user, OpenViduRole role, String roomId) {

        String serverData = "{\"serverData\": \"" + "userId_" + user.getId() + "\"}";

        // opeinvidu 연결 속성 생성
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .role(role)
                .build();

        if (this.mapSessions.get(roomId) != null) {
            // 화상 채팅방이 이미 존재 할 경우
            try {
                // 방이 생성되어 있는 경우, 그 방으로 들어가는 token 생성
                String token = this.mapSessions.get(roomId).createConnection(connectionProperties).getToken();
                // 토큰 저장 된 컬렉션 업데이트
                this.mapSessionNamesTokens.get(roomId).put(token, role);
                return token;

            } catch (OpenViduJavaClientException e1) {
                // openvidu 내부 오류
                throw new ChatApiException(e1.getMessage());

            } catch (OpenViduHttpException e2) {
                if (404 == e2.getStatus()) {
                    // 잘못된 sessionId ( ex. 유저가 예기치 않게 방을 떠남) -> session 삭제
                    this.mapSessions.remove(roomId);
                    this.mapSessionNamesTokens.remove(roomId);
                }
            }
        }

        // 화상 채팅 방이 없을 경우 새로 생성
        try {
            // openvidu 세션 생성
            Session session = this.openVidu.createSession();
            // 새로 생성된 openvidu 세션에 연결 토큰 생성
            String token = session.createConnection(connectionProperties).getToken();
            //방 관리 map에 저장 채팅방 ID랑 들어온 유저 저장
            this.mapSessions.put(roomId, session);
            this.mapSessionNamesTokens.put(roomId, new ConcurrentHashMap<>());
            this.mapSessionNamesTokens.get(roomId).put(token, role);

            return token;

        } catch (Exception e) {
            throw new ChatApiException(e.getMessage());
        }
    }

    // user 정보 확인
    private User userCheck(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new ChatApiException("해당 유저를 찾을 수 없습니다."));
    }

    // post 정보 확인
    private Post postCheck(Long postId) {
        return postRepository.findByIdAndIsLetterFalse(postId);
    }

}
