package com.sparta.meeting_platform.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   // Stomp 를 사용하기위해 선언하는 어노테이션
public class ChatConfig implements WebSocketMessageBrokerConfigurer {

    // Client 에서 websocket 연결할 때 사용할 API 경로를 설정해주는 메서드.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS Client 가 웹소켓 핸드셰이크 커넥션을 생성할 EndpointURL 지정
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins("http://13.125.151.93")
                .setAllowedOrigins("http://localhost:3000")
                .setAllowedOrigins("http://localhost:8080")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지 받을 때 관련 경로 설정
        // "/queue", "/topic" 경로로 SimpleBroker 를 등록.
        // SimpleBroker 는 해당하는 경로를 SUBSCRIBE 하는 Client 에게 메세지를 전달하는 간단한 작업을 수행
        // "/queue", "/topic" 이 두 경로가 prefix(api 경로 맨 앞)에 붙은 경우,
        // messageBroker 가 잡아서 해당 채팅방을 구독하고 있는 클라이언트에게 메시지를 전달해줌
        // 주로 "/queue"는 1 대 1 메시징, "/topic"은 1 대 N 메시징일 때 주로 사용함.
        registry.enableSimpleBroker("/queue", "/topic");

        // Client 에서 SEND 요청을 처리
        // 메시지 보낼 때 관련 경로 설정
        registry.setApplicationDestinationPrefixes("/app");
    }

}