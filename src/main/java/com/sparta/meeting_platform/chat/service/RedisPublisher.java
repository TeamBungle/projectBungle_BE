package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    /*
     * websocket 에서 받아온 메세지를 convertAndsend를 통하여 Redis의 메세지 리스너로 발행
     */
    public void publish(ChannelTopic topic, ChatMessageDto messageDto) {
        redisTemplate.convertAndSend(topic.getTopic(), messageDto);
    }

}
