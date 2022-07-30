package com.sparta.meeting_platform.chat.service;

import com.sparta.meeting_platform.chat.dto.NotificationDto;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.ChatMessageJpaRepository;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.domain.Post;
import com.sparta.meeting_platform.exception.PostApiException;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.util.NotificationComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;

    private final PostRepository postRepository;

    @Transactional
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        Boolean readCheck = false;

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByUserIdAndReadCheck(userId, readCheck);

        for (InvitedUsers invitedUser : invitedUsers) {
            List<ChatMessage> findChatMessageDtoList = chatMessageJpaRepository.findAllByRoomId(String.valueOf(invitedUser.getPostId()));
            for (ChatMessage findChatMessageDto : findChatMessageDtoList) {
                if (Objects.equals(String.valueOf(invitedUser.getPostId()), findChatMessageDto.getRoomId())) {
                    if (invitedUser.getReadCheckTime().isBefore(findChatMessageDto.getCreatedAt())) {
                        Post post = postRepository.findById(Long.valueOf(findChatMessageDto.getRoomId())).orElseThrow(
                                () -> new PostApiException("존재하지 않는 게시물 입니다.")
                        );
                        NotificationDto notificationDto = new NotificationDto();
                        if(findChatMessageDto.getMessage().isEmpty()){
                            notificationDto.setMessage("파일이 왔어요😲");
                        }else {
                            notificationDto.setMessage(findChatMessageDto.getMessage());
                        }
                        notificationDto.setNickname(findChatMessageDto.getSender());
                        notificationDto.setCreatedAt(findChatMessageDto.getCreatedAt());
                        notificationDto.setRoomId(findChatMessageDto.getRoomId());
                        notificationDto.setTitle(post.getTitle());
                        notificationDtoList.add(notificationDto);
                    }
                }
            }
        }
        notificationDtoList.sort(new NotificationComparator());
        return notificationDtoList;
    }
}

