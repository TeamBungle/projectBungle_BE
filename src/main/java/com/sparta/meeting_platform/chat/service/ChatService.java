package com.sparta.meeting_platform.chat.service;

import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.dto.RoomIdDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.repository.ChatMessageRepository;
import com.sparta.meeting_platform.chat.repository.ChatRoomRepository;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations sendingOperations; // 특정 Broker 로 메세지를 전달

    private Map<String, ChatRoom> chatRooms;


    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 불러오기
    public List<ChatRoom> findAllRoom() {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> result = new ArrayList<>(chatRooms.values());
        Collections.reverse(result);
        return result;
    }

    //채팅방 하나 불러오기
    public ChatRoom findById(String roomId) {
        return chatRooms.get(roomId);
    }

    //채팅방 생성
    public ChatRoom createRoom(Long postId, RoomIdDto roomId) {
        Optional<Post> post = postRepository.findById(postId);
        String s=roomId.getRoomId();
        ChatRoom chatRoom = ChatRoom.builder()
               .roomId(s)
               .title(post.get().getTitle())
               .personnel(post.get().getPersonnel())
               .build();
        chatRoomRepository.save(chatRoom);
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }


    /**
     * 채팅방에 메시지 발송
     */

    public void enter(ChatMessageDto message, String token) {
        // 토큰으로 유저정보 가져오기
        UserDetailsImpl userDetails = (UserDetailsImpl) jwtTokenProvider.getAuthentication(token).getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        message.setNickName(user.getNickName());
        message.setProfileUrl(user.getProfileUrl());

        // 유저 카운트 설정
//        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));

        ChatMessage chatMessage = ChatMessage.builder()
                .type(message.getType())
                .roomId(message.getRoomId())
                .nickName(message.getNickName())
                .message(message.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getNickName() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
            message.setMessage(message.getNickName() + "님이 방에서 나갔습니다.");
        }

        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);

    }


}