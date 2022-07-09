package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String,Object> redisTemplate;


    public void publish (ChannelTopic topic , ChatMessage message) {
        log.info("ChannelTopic : {}", topic.getTopic());
        log.info("ChatMessage : {}", message.getType());
        redisTemplate.convertAndSend(topic.getTopic(),message);
        log.info("발행 완료!");
    }

}
