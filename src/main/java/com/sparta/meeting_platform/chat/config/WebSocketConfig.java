package com.sparta.meeting_platform.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
* Websocket 관련 설정들을 모아놓은 Class
* */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    /*
    * Websocket HandShake 를 위한 EndPoint를 지정하고 CORS 설정 및 SockJS 사용 설정
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")
//                .setAllowedOriginPatterns("https://jeju.project.s3-website.ap-northeast-2.amazonaws.com/")
                .setAllowedOriginPatterns("https://localhost:3000/")
                .withSockJS();
    }

    /*
    * Stomp 사용을 위한 Message Broker 설정을 해주는 메소드이다.
    * 1.enableSimpleBroker
    * 메세지를 받을때, 경로를 설정해준다
    * "/sub"이 api에 prefix로 붙은경우, messagebroker가 해당 경로를 가로챈다
    * 2.setApplicationDestinationPrefixes
    * - 메세지를 보낼때 관련 경로를 설정해주는 함수.
    * - 클라이언트가 메세지를 보낼때, api에 prefix로 "/pub"이 붙어있으면 broker로 메세지가 보내진다.
    *  */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/pub")
                .enableSimpleBroker("/sub");
    }

    /*
    * interceptors 설정
    */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(stompHandler);
    }
}
