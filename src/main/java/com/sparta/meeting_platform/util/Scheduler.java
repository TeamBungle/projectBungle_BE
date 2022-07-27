package com.sparta.meeting_platform.util;

import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.model.ResignChatMessage;
import com.sparta.meeting_platform.chat.model.ResignChatRoom;
import com.sparta.meeting_platform.chat.repository.*;
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
    // 초, 분, 시, 일, 월, 주 순서
    @Scheduled(cron = "0 */1 * * * *") // 1분마다
    @Transactional
    public void deletePost(){
        System.out.println("삭제실행");
        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime postLocalDateTime = LocalDateTime.parse(post.getTime(), inputFormat);
            if (postLocalDateTime.plusMinutes(10).isBefore(LocalDateTime.now())) {
                User user = post.getUser();
                user.setIsOwner(true);
                ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(String.valueOf(post.getId()));
                List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByChatRoom(chatRoom);
                ResignChatRoom resignChatRoom = new ResignChatRoom(chatRoom);
                resignChatRoomJpaRepository.save(resignChatRoom);

                for (ChatMessage message : chatMessage) {
                    ResignChatMessage resignChatMessage = new ResignChatMessage(message);
                    resignChatMessageJpaRepository.save(resignChatMessage);
                }
//                chatMessageJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
                chatMessageJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
                chatRoomJpaRepository.deleteByRoomId(String.valueOf(post.getId()));
                likeRepository.deleteByPostId(post.getId());
                invitedUsersRepository.deleteAllByPostId(post.getId());
                postRepository.deleteById(post.getId());
            }
        }
    }
}
