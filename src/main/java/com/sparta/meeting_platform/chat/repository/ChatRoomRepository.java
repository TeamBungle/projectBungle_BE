package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.dto.ChatRoomResponseDto;
import com.sparta.meeting_platform.chat.dto.UserDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.service.RedisSubscriber;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final PostRepository postRepository;
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final UserRepository userRepository;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private static Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    //내가 참여한 모든 채팅방 목록 조히
    @Transactional
    public List<ChatRoomResponseDto> findAllRoom(Long userId) {
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByUserId(userId);
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        for (InvitedUsers invitedUsers1 : invitedUsers) {
                Post post = postRepository.findByUserId(invitedUsers1.getUser().getId());
                ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto();
                chatRoomResponseDto.setPostUrl(post.getPostUrls().get(0));
                chatRoomResponseDto.setLetter(post.getIsLetter());
                chatRoomResponseDto.setPostTitle(post.getTitle());
                chatRoomResponseDto.setPostCreatedAt(post.getCreatedAt());
                ChatMessage chatMessage = chatMessageJpaRepository.findTop1ByRoomIdOrderByCreatedAtDesc(invitedUsers1.getRoomId());
                if (chatMessage.getMessage() != null) {
                    chatRoomResponseDto.setLastMessage(chatMessage.getMessage());
                } else {
                    chatRoomResponseDto.setLastMessage("파일 전송");
                }
                chatRoomResponseDto.setLastMessageTime(chatMessage.getCreatedAt());
                chatRoomResponseDtoList.add(chatRoomResponseDto);
            }
        return chatRoomResponseDtoList;
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }
    /*
    * 채팅방 생성 , 게시글 생성시 만들어진 postid를 받아와서 게시글 id로 사용한다.
    */
    @Transactional
    public void createChatRoom(Post post, UserDto userDto) {
        ChatRoom chatRoom = ChatRoom.create(post, userDto);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom); // redis 저장
        chatRoomJpaRepository.save(chatRoom); // DB 저장
    }

    public static ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
