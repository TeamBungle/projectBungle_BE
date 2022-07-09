package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());

        String username = jwtTokenProvider.getUserPk(token); // 토큰에서 유저 아이디 가져오기

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
        );
        ChatMessage chatMessage = new ChatMessage(messageDto);
        chatMessage.setSender(user.getNickName());
        chatMessage.setProfileUrl(user.getProfileUrl());
        chatMessage.setEnterUserCnt(enterUserCnt);
//        Date date = new Date();
//        chatMessage.setCreateAt(date); // 시간세팅

        log.info("type : {}", chatMessage.getType());

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatRoomRepository.enterChatRoom(chatMessage.getRoomId());

            chatMessage.setMessage("[알림] " + chatMessage.getSender() + "님이 입장하셨습니다.");
            chatMessage.setProfileUrl(null);

        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {

            chatMessage.setMessage("[알림] " + chatMessage.getSender() + "님이 나가셨습니다.");
            chatMessage.setProfileUrl(null);
        }

        log.info("ENTER : {}", chatMessage.getMessage());

        chatMessageRepository.save(chatMessage); // 캐시에 저장 했다.
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
    }


    //redis에 저장되어있는 message 들 출력
    public List<ChatMessage> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }

}

