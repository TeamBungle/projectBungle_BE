package com.sparta.meeting_platform.chat.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
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

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChatMessageRepository {        //redis

    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    private final ChatMessageMysqlRepository chatMessageMysqlRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessage;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    public Long getUserCnt(String roomId){
        log.info("getUserCnt : {}", Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0")));
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    @Transactional
    public ChatMessageDto save(ChatMessageDto chatMessageDto) {
        log.info("chatMessage : {}", chatMessageDto.getMessage());
        log.info("type: {}", chatMessageDto.getType());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class)); // 왜 ?

        String roomId = chatMessageDto.getRoomId();
        List<ChatMessageDto> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        if (chatMessageList == null) chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessageDto);
        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        log.info("list size : {}", (opsHashChatMessage.get(CHAT_MESSAGE, roomId)).size());
        if((opsHashChatMessage.get(CHAT_MESSAGE, roomId)).size() % 10 == 0){
            log.info("Save DB!");
            List<ChatMessageDto> chatMessageDtos = opsHashChatMessage.get(CHAT_MESSAGE, roomId);
            ObjectMapper mapper = new ObjectMapper();
            List<ChatMessageDto> chatMessageDtos1 = mapper.convertValue(chatMessageDtos, new TypeReference<List<ChatMessageDto>>(){});
            for (ChatMessageDto messageDto : chatMessageDtos1) {
                ChatMessage chatMessage = new ChatMessage(messageDto);
                chatMessageMysqlRepository.save(chatMessage);
            }
            opsHashChatMessage.delete(CHAT_MESSAGE,roomId);
        }
        return chatMessageDto;
    }

    //채팅 가져오기 from redis
    public List<ChatMessageDto> findAllMessage(String roomId) {
        log.info("findAllMessage");
        return opsHashChatMessage.get(CHAT_MESSAGE, roomId);
    }

    public void setUserEnterInfo(String roomId, String sessionId) {
        hashOpsEnterInfo.put(ENTER_INFO,sessionId,roomId);
        log.info("hashPosEnterInfo.put : {}",hashOpsEnterInfo.get(ENTER_INFO, sessionId) );
    }

    public void plusUserCnt(String roomId) {
        valueOps.increment(USER_COUNT + "_" + roomId); // redis string type에서 사용하는 increment 함수, 유저 카운트 증
    }

    public void minusUserCnt(String sessionId, String roomId) {
//        String redisRoomId = getRoomId(sessionId);
//        if(!roomId.equals(redisRoomId)) { // 유저 카운터 감소
//            return;
//        }
        Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0);
    }

    public String getRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
        if(hashOpsEnterInfo.get(ENTER_INFO, sessionId) == null) {
            log.info("세션 삭제 완료 : {}", sessionId);
        }
    }

}
