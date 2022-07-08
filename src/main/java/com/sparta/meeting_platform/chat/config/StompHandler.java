package com.sparta.meeting_platform.chat.config;

import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println(accessor);
        System.out.println("stomp handler");
        System.out.println(message);
// websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("stomphandler-1" + accessor.getCommand());
            System.out.println("stomphandler-2" + accessor.getFirstNativeHeader("Authorization"));
            String token = accessor.getFirstNativeHeader("Authorization");
            String jwtToken = token.replace("Bearer ", "");
            jwtTokenProvider.validateToken(jwtToken);
        }
        return message;
    }
}
