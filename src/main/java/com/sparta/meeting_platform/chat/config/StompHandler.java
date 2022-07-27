package com.sparta.meeting_platform.chat.config;

import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.service.ChatRoomService;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import java.util.Optional;


// Websocket 을 통하여 요청이 들어오면 Intercept 하여 JWt 인증 구현 및 사전처리
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = (String) message.getHeaders().get("simpSessionId");

         // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token"));

         // 구독 요청시 유저의 카운트수를 저장하고 최대인원수를 관리하며 , 세션정보를 저장한다.
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String roomId = chatRoomService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            chatMessageRepository.plusUserCnt(roomId);
            chatMessageRepository.setUserEnterInfo(roomId, sessionId);

         // 채팅방 나간 유저의 카운트 수를 반영하고, 방에서 세션정보를 지움
        } else if (StompCommand.UNSUBSCRIBE == accessor.getCommand() || StompCommand.DISCONNECT == accessor.getCommand()) { // UNSUBSCRIBE 사용 안함
            String roomId = chatRoomService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            chatMessageRepository.removeUserEnterInfo(sessionId, roomId);
            chatMessageRepository.minusUserCnt(roomId);
        }
        return message;
    }
}

