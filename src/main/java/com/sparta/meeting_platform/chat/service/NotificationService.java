package com.sparta.meeting_platform.chat.service;

import com.sparta.meeting_platform.chat.dto.FindChatMessageDto;
import com.sparta.meeting_platform.chat.dto.NotificationDto;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.ChatMessageJpaRepository;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
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

    @Transactional
    public List<NotificationDto> getNotification(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        Boolean readCheck = false;

        List<NotificationDto> notificationDtoList = new ArrayList<>();
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByUserIdAndReadCheck(userId, readCheck);

        for (InvitedUsers invitedUser : invitedUsers) {
            List<FindChatMessageDto> findChatMessageDtoList = chatMessageJpaRepository.findAllByRoomId(String.valueOf(invitedUser.getPostId()));
            for (FindChatMessageDto findChatMessageDto : findChatMessageDtoList) {
                if (Objects.equals(String.valueOf(invitedUser.getPostId()), findChatMessageDto.getRoomId())) {
                    if (invitedUser.getReadCheckTime().isBefore(findChatMessageDto.getCreatedAt())) {
                        NotificationDto notificationDto = new NotificationDto();
                        notificationDto.setMessage(findChatMessageDto.getMessage());
                        notificationDto.setNickname(findChatMessageDto.getSender());
                        notificationDto.setCreatedAt(findChatMessageDto.getCreatedAt());
                        notificationDto.setRoomId(findChatMessageDto.getRoomId());
                        notificationDtoList.add(notificationDto);
                    }
                }
            }
        }
        notificationDtoList.sort(new NotificationComparator());
        return notificationDtoList;
    }
}

