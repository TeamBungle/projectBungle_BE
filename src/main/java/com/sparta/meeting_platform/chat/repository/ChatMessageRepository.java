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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChatMessageRepository {

    private static final String CHAT_MESSAGE = "CHAT_MESSAGE"; // 채팅룸에 메세지들을 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate; // redisTemplate 사용
    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private HashOperations<String, String, String> hashOpsEnterInfo; // Redis 의 Hashes 사용
    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessage; // Redis 의 Hashes 사용
    private ValueOperations<String, String> valueOps; // Redis 의 String 구조 사용

    //초기화
    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    //유저 카운트 받아오기
    public Long getUserCnt(String roomId) {
        log.info("getUserCnt : {}", Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0")));
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    //redis 에 메세지 저장하기
    @Transactional
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        log.info("chatMessage : {}", chatMessageDto.getMessage());
        log.info("type: {}", chatMessageDto.getType());
        //chatMessageDto 를 redis 에 저장하기 위하여 직렬화 한다.
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessageDto.getRoomId();
        //redis에 저장되어있는 리스트를 가져와, 새로 받아온 chatmessageDto를 더하여 다시 저장한다.
        List<ChatMessageDto> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);
        //가져온 List가 null일때 새로운 리스트를 만든다 == 처음에 메세지를 저장할경우 리스트가 없기때문에.
        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessageDto);
        //redis 의 hashes 자료구조
        //key : CHAT_MESSAGE , filed : roomId, value : chatMessageList
        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        redisTemplate.expire(CHAT_MESSAGE,30, TimeUnit.MINUTES);
        return chatMessageDto;
    }

    //채팅 리스트 가져오기
    @Transactional
    public List<ChatMessageDto> findAllMessage(String roomId) {
        log.info("findAllMessage");
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();
        //chatMessage 리스트를 불러올때, 리스트의 사이즈가 0보다 크면 redis 정보를 가져온다
        //redis 에서 가져온 리스트의 사이즈가  0보다 크다 == 저장된 정보가 있다.
        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
            return (opsHashChatMessage.get(CHAT_MESSAGE, roomId));
        } else { // redis 에서 가져온 메세지 리스트의 사이즈가 0보다 작다 == redis에 정보가 없다.
            List<FindChatMessageDto> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);

            for (FindChatMessageDto chatMessage : chatMessages) {
                ChatMessageDto chatMessageDto = new ChatMessageDto(chatMessage);
                chatMessageDtoList.add(chatMessageDto);
            }
            //redis에 정보가 없으니, 다음부터 조회할때는 redis를 사용하기 위하여 넣어준다.
            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageDtoList);
            return chatMessageDtoList;
        }
    }

    // 구독 요청시
    public void setUserEnterInfo(String roomId, String sessionId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
        log.info("hashPosEnterInfo.put : {}", hashOpsEnterInfo.get(ENTER_INFO, sessionId));
    }

    // 구독시 유저 카운트 증가
    public void plusUserCnt(String roomId) {
        valueOps.increment(USER_COUNT + "_" + roomId); // redis string type에서 사용하는 increment 함수, 유저 카운트 증가
    }

    // unsubscribe 시 유저 카운트 감소
    public void minusUserCnt(String roomId) {
        Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0);
    }

    //sessionId 로 roomId 가져오기
    public String getRoomsId(String sessionId) {
        log.info("세션당 채팅방 :" + sessionId);
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId, roomId);
        log.info("hashPosEnterInfo.put : {}", hashOpsEnterInfo.get(ENTER_INFO, sessionId));
    }

}
