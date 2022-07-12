package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.dto.ChatRoomResponseDto;
import com.sparta.meeting_platform.chat.dto.FindChatMessageDto;
import com.sparta.meeting_platform.chat.dto.UserDto;
import com.sparta.meeting_platform.chat.model.ChatRoom;
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
    private final UserRepository userRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
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

    public List<ChatRoomResponseDto> findAllRoom(Long userId) {
        String username = userRepository.findById(userId).get().getUsername();
        List<ChatRoom> chatRoomList = chatRoomJpaRepository.findAllByUsername(username);
        List<String> chatMessage = new ArrayList<>();
        List<String> chatMessageCreatedAt = new ArrayList<>();
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        List<Post> postList = postRepository.findAllByUserId(userId);

        for (ChatRoom chatRoom : chatRoomList) {
            String roomId = chatRoom.getRoomId();
            List<FindChatMessageDto> findMessageInfos = chatMessageJpaRepository.findAllByRoomId(roomId);

            for (FindChatMessageDto findMessageInfo : findMessageInfos) {
                chatMessage.add(findMessageInfo.getMessage());
                chatMessageCreatedAt.add(findMessageInfo.getCreatedAt());
            }
        }

        for (Post post : postList) {
            ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto();
            chatRoomResponseDto.setPostUrl(post.getPostUrls().get(0));
            chatRoomResponseDto.setLetter(post.getIsLetter());
            chatRoomResponseDto.setPostTitle(post.getTitle());
            chatRoomResponseDto.setLastMessage(chatMessage.get(chatMessage.size() - 1));
            chatRoomResponseDto.setLastMessageTime(chatMessageCreatedAt.get(chatMessageCreatedAt.size() - 1));
            chatRoomResponseDto.setPostCreatedAt(post.getCreatedAt());
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

    public ChatRoom createChatRoom(Post post, UserDto userDto) {
        ChatRoom chatRoom = ChatRoom.create(post,userDto);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom); // redis 저장
        chatRoomJpaRepository.save(chatRoom); // DB 저장
        return chatRoom;
    }

    public static ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
