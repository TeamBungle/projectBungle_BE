package com.sparta.meeting_platform.chat.controller;

import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MessageController {

    private final JwtTokenProvider jwtTokenProvider;

    private final SimpMessageSendingOperations sendingOperations; // 특정 Broker 로 메세지를 전달

    // Client 가 SEND 할 수 있는 경로
    // stompConfig 에서 설정한 applicationDestinationPrefixes 와 @MessageMapping 경로가 병합됨
    // Client 에서는 prefix 를 붙여"/chat/room/enter"
    // "/app/chat/message"로 발행 요청을 하면 Controller 가 해당 메세지를 받아 처리하는데,
    // 메세지가 발행되면 "/topic/chat/room/[roomId]"로 메세지가 전송되는 것을 볼 수 있다.

    // Client 에서는 해당 주소를 SUBSCRIBE 하고 있다가 메세지가 전달되면 화면에 출력한다.
    // 이때 /topic/chat/room/[roomId]는 채팅방을 구분하는 값이다.
    // 기존의 핸들러 ChatHandler 의 역할을 대신 해주므로 핸들러는 없어도 된다.
    @MessageMapping("/chat/message")    // WebSocket 으로 들어오는 메세지 발행을 처리한다.
    public void enter(ChatMessageDto message, @Header("token") String token) {


        if (ChatMessageDto.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getNickName() + "님이 입장하였습니다.");
        }
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}