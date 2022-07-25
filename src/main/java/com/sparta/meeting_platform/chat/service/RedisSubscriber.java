package com.sparta.meeting_platform.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    // Redis 에서 메시지가 발행(publish)되면 대기하고 있던 onMessage 가 해당 메시지를 받아 처리한다.
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}