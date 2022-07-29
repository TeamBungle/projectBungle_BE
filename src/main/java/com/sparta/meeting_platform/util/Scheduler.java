package com.sparta.meeting_platform.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.model.ResignChatMessage;
import com.sparta.meeting_platform.chat.model.ResignChatRoom;
import com.sparta.meeting_platform.chat.repository.*;
import com.sparta.meeting_platform.chat.service.RedisPublisher;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final PostRepository postRepository;
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final ResignChatRoomJpaRepository resignChatRoomJpaRepository;
    private final ResignChatMessageJpaRepository resignChatMessageJpaRepository;
    private final LikeRepository likeRepository;
    private final RedisPublisher redisPublisher;
    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 */1 * * * *") // 1분마다
    @Transactional
    public void deletePost() throws JsonProcessingException {
        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(post.getTime(), inputFormat);
            if (localDateTime.plusHours(24).isBefore(LocalDateTime.now())) {
                User user = post.getUser();
                user.setIsOwner(false);
                ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(String.valueOf(post.getId()));
                List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(String.valueOf(post.getId()));
                ResignChatRoom resignChatRoom = new ResignChatRoom(chatRoom);
                resignChatRoomJpaRepository.save(resignChatRoom);

                for (ChatMessage message : chatMessage) {
                    ResignChatMessage resignChatMessage = new ResignChatMessage(message);
                    resignChatMessageJpaRepository.save(resignChatMessage);
                }
                chatMessageJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
                chatRoomJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
                if (invitedUsersRepository.existsByPostId(post.getId())) {
                    invitedUsersRepository.deleteAllByPostId(post.getId());
                }
                likeRepository.deleteByPostId(post.getId());
                postRepository.deleteById(post.getId());
                LocalDateTime createdAt = LocalDateTime.now();
                String createdAtString = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
                ChatMessageDto chatMessageDto = new ChatMessageDto();
                chatMessageDto.setType(ChatMessage.MessageType.QUIT);
                chatMessageDto.setQuitOwner(true);
                chatMessageDto.setRoomId(String.valueOf(post.getId()));
                chatMessageDto.setUserId(user.getId());
                chatMessageDto.setSender(user.getNickName());
                chatMessageDto.setProfileUrl(user.getProfileUrl());
                chatMessageDto.setCreatedAt(createdAtString);
                chatMessageDto.setMessage("[알림] " + "약속시간 이후 24시간이 지나 더 이상 대화를 할 수 없으며 채팅방을 나가면 다시 입장할 수 없습니다.");
                redisPublisher.publish(ChatRoomRepository.getTopic(String.valueOf(post.getId())), chatMessageDto);
            }
        }
    }
}
