package com.sparta.meeting_platform.chat.repository;

import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.dto.FindChatMessageDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE"; // 채팅룸에 메세지들을 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private final RedisTemplate<String, Object> redisTemplate; // redisTemplate 사용
    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private HashOperations<String, String, String> hashOpsEnterInfo; // Redis 의 Hashes 사용
    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessage; // Redis 의 Hashes 사용
    private ValueOperations<String, String> valueOps; // Redis 의 String 구조 사용

    // Redis 초기화
    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    // 유저 카운트 받아오기
    public Long getUserCnt(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    //redis 에 메세지 저장하기
    @Transactional
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessageDto.getRoomId();
        List<ChatMessageDto> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessageDto);
        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        redisTemplate.expire(CHAT_MESSAGE, 24, TimeUnit.HOURS);
        return chatMessageDto;
    }

    // redis 에 저장되어있는 message 들 출력
    @Transactional
    public List<ChatMessageDto> getMessage(String roomId) {
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();

        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
            return (opsHashChatMessage.get(CHAT_MESSAGE, roomId));
        } else {
            List<FindChatMessageDto> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);

            for (FindChatMessageDto chatMessage : chatMessages) {
                LocalDateTime createdAt = chatMessage.getCreatedAt();
                String createdAtString = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
                ChatMessageDto chatMessageDto = new ChatMessageDto(chatMessage, createdAtString);
                chatMessageDtoList.add(chatMessageDto);
            }
            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageDtoList);
            return chatMessageDtoList;
        }
    }

    // 구독 요청시
    public void setUserEnterInfo(String roomId, String sessionId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 카운트 증가
    public void plusUserCnt(String roomId) {
        valueOps.increment(USER_COUNT + "_" + roomId);
    }

    // 카운트 감소
    public void minusUserCnt(String roomId) {
        Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0);
    }

    // 유저 세션정보 삭제
    public void removeUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId, roomId);
    }

}
