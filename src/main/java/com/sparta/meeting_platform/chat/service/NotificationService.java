package com.sparta.meeting_platform.chat.service;

import com.sparta.meeting_platform.chat.dto.FindChatMessageDto;
import com.sparta.meeting_platform.chat.dto.NotificationDto;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.ChatMessageJpaRepository;
import com.sparta.meeting_platform.chat.repository.InvitedUsersRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    public List<NotificationDto> getNoti(UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        Boolean readCheck =false;
        HashMap<String , List<FindChatMessageDto>> chatMessages = new HashMap<>();
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByUserIdAndReadCheck(userId,readCheck);
        for (InvitedUsers invitedUser : invitedUsers) {
            List<FindChatMessageDto> chatMessage = chatMessageJpaRepository.findAllByRoomId(String.valueOf(invitedUser.getPostId()));
            chatMessages.put(String.valueOf(invitedUser.getPostId()),chatMessage);
        }
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for (HashMap.Entry<String, List<FindChatMessageDto>> chatMessageEntry : chatMessages.entrySet()) {
            List<FindChatMessageDto> findChatMessageDtoList = chatMessageEntry.getValue();
            for (FindChatMessageDto findChatMessageDto : findChatMessageDtoList) {
                for(int i = 0; i < findChatMessageDtoList.size(); i++){
                    Date date1 = new Date(String.valueOf(invitedUsers.get(i).getReadCheck()));
                    Date date2 = new Date(String.valueOf(findChatMessageDto.getCreatedAt()));
                    if(date1.before(date2)){
                        NotificationDto notificationDto = new NotificationDto();
                        notificationDto.setMessage(findChatMessageDto.getMessage());
                        notificationDto.setNickname(findChatMessageDto.getSender());
                        notificationDto.setCreatedAt(findChatMessageDto.getCreatedAt());
                        notificationDtoList.add(notificationDto);
                    }
                }
            }

        }
    return notificationDtoList;
    }
}
