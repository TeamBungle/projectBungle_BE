package com.sparta.meeting_platform.chat.config;

import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.chat.service.ChatRoomService;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.exception.ChatApiException;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/*
* Websocket 을 통하여 요청이 들어오면 Intercept 하여 JWt 인증 구현 및 사전처리
*/
@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;
    private final PostRepository postRepository;
    private final InvitedUsersRepository invitedUsersRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("simpDestination : {}", message.getHeaders().get("simpDestination"));
        log.info("sessionId : {}", message.getHeaders().get("simpSessionId"));
        String sessionId = (String) message.getHeaders().get("simpSessionId");

        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("CONNECT : {}", sessionId);
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token"));
            // 구독 요청시 유저의 카운트수를 저장하고 최대인원수를 관리하며 , 세션정보를 저장한다.
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("SUBSCRIBE : {}", sessionId);
            String roomId = chatRoomService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            log.info("roomId : {}", roomId);
            chatMessageRepository.setUserEnterInfo(roomId, sessionId);
            chatMessageRepository.plusUserCnt(roomId);
            Long postId = Long.valueOf(roomId);
            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new PostApiException("존재하지 않는 게시물 입니다!")
            );
            List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(postId);
            int nowPeople = invitedUsers.size();
            int maxPeople = post.getPersonnel();
            if (nowPeople >= maxPeople) {
                throw new ChatApiException("채팅방 정원 초과!");
            }
            // 구독자가 연결을 끊을시, 저장해두었던 세션정보를 삭제하고, 인원수를 줄여준다.
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("DISCONNECT : {}", sessionId);
            String roomId = chatMessageRepository.getRoomId(sessionId);
            log.info("roomId: {}", roomId);
            chatMessageRepository.minusUserCnt(roomId);
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatMessageRepository.removeUserEnterInfo(sessionId);
        }
        return message;
    }
}

