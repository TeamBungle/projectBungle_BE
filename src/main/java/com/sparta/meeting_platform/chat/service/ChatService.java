package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.repository.ChatMessageMysqlRepository;
import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ChatMessageMysqlRepository chatMessageMysqlRepository;

    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());

        String username = jwtTokenProvider.getUserPk(token); // 토큰에서 유저 아이디 가져오기

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
        );

        messageDto.setSender(user.getNickName());
        messageDto.setProfileUrl(user.getProfileUrl());
        messageDto.setEnterUserCnt(enterUserCnt);
        messageDto.setUsername(username);
        DateFormat dateFormat = new SimpleDateFormat("dd,MM,yyyy,HH,mm,ss", Locale.KOREA);
//        TimeZone time;
//        time = TimeZone.getTimeZone("Asia/seoul");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.getTimeInMillis());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateToStr = dateFormat.format(date);
        messageDto.setCreatedAt(dateToStr);


        log.info("type : {}", messageDto.getType());

        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
            chatRoomRepository.enterChatRoom(messageDto.getRoomId());

            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 입장하셨습니다.");
            messageDto.setProfileUrl(null);

        } else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {

            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 나가셨습니다.");
            messageDto.setProfileUrl(null);
        }

        log.info("ENTER : {}", messageDto.getMessage());

        chatMessageRepository.save(messageDto); // 캐시에 저장 했다.
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
    }


    //redis에 저장되어있는 message 들 출력
    public List<ChatMessageDto> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }

}

