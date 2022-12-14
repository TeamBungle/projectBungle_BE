package com.sparta.meeting_platform.chat.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.meeting_platform.chat.dto.ChatMessageDto;
import com.sparta.meeting_platform.chat.dto.FilesDto;
import com.sparta.meeting_platform.chat.dto.UserDetailDto;
import com.sparta.meeting_platform.chat.dto.UserinfoDto;
import com.sparta.meeting_platform.chat.model.*;
import com.sparta.meeting_platform.chat.repository.*;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.LikeRepository;
import com.sparta.meeting_platform.repository.PostRepository;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final S3Service s3Service;
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ResignChatRoomJpaRepository resignChatRoomJpaRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final ResignChatMessageJpaRepository resignChatMessageJpaRepository;


    @Transactional
    public void save(ChatMessageDto messageDto, Long pk) throws JsonProcessingException {
        // ???????????? ?????? ????????? ????????????
        User user = userRepository.findById(pk).orElseThrow(
                () -> new NullPointerException("???????????? ?????? ????????? ?????????!")
        );
        LocalDateTime createdAt = LocalDateTime.now();
        String formatDate = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());
        messageDto.setEnterUserCnt(enterUserCnt);
        messageDto.setSender(user.getNickName());
        messageDto.setProfileUrl(user.getProfileUrl());
        messageDto.setCreatedAt(formatDate);
        messageDto.setUserId(user.getId());
        messageDto.setQuitOwner(false);

        //????????? ???????????? ????????? ENTER ??????
        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
            chatRoomRepository.enterChatRoom(messageDto.getRoomId());
            messageDto.setMessage( messageDto.getSender() + "?????? ?????????????????????.");
            String roomId = messageDto.getRoomId();


            List<InvitedUsers> invitedUsersList = invitedUsersRepository.findAllByPostId(Long.parseLong(roomId));
            for (InvitedUsers invitedUsers : invitedUsersList) {
                if (invitedUsers.getUser().equals(user)) {
                    invitedUsers.setReadCheck(true);
                }
            }
            // ?????? ????????? ???????????? ????????? ???????????? ????????? ?????? ?????? ??????.
            if (!invitedUsersRepository.existsByUserIdAndPostId(user.getId(), Long.parseLong(roomId))) {
                InvitedUsers invitedUsers = new InvitedUsers(Long.parseLong(roomId), user);
                invitedUsersRepository.save(invitedUsers);
            }
            //????????? ????????? ????????? QUIT ??????
        } else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {
            messageDto.setMessage(messageDto.getSender() + "?????? ??????????????????.");
            if (invitedUsersRepository.existsByUserIdAndPostId(user.getId(), Long.parseLong(messageDto.getRoomId()))) {
                invitedUsersRepository.deleteByUserIdAndPostId(user.getId(), Long.parseLong(messageDto.getRoomId()));
            }
            if (!postRepository.existsById(Long.parseLong(messageDto.getRoomId()))) {
                ResignChatRoom chatRoom = resignChatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
                if (chatRoom.getUsername().equals(user.getUsername())) {
                    messageDto.setQuitOwner(true);
                    messageDto.setMessage("(??????) " + messageDto.getSender() + "?????? ??????????????????. " +
                            "??? ?????? ????????? ??? ??? ????????? ???????????? ????????? ?????? ????????? ??? ????????????.");
                    likeRepository.deleteByPostId(Long.parseLong(messageDto.getRoomId()));
                    postRepository.deleteById(Long.parseLong(messageDto.getRoomId()));
                    user.setIsOwner(false);
                    ChatRoom findChatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
                    List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(messageDto.getRoomId());
                    ResignChatRoom resignChatRoom = new ResignChatRoom(findChatRoom);
                    resignChatRoomJpaRepository.save(resignChatRoom);
                    for (ChatMessage message : chatMessage) {
                        ResignChatMessage resignChatMessage = new ResignChatMessage(message);
                        resignChatMessageJpaRepository.save(resignChatMessage);
                    }
                    chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
                    chatRoomJpaRepository.deleteByRoomId(messageDto.getRoomId());
                }
            }else {
                ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
                if (chatRoom.getUsername().equals(user.getUsername())) {
                    messageDto.setQuitOwner(true);
                    messageDto.setMessage("(??????) " + messageDto.getSender() + "?????? ??????????????????. " +
                            "??? ?????? ????????? ??? ??? ????????? ???????????? ????????? ?????? ????????? ??? ????????????.");
                    likeRepository.deleteByPostId(Long.parseLong(messageDto.getRoomId()));
                    postRepository.deleteById(Long.parseLong(messageDto.getRoomId()));
                    user.setIsOwner(false);
                    ChatRoom findChatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());
                    List<ChatMessage> chatMessage = chatMessageJpaRepository.findAllByRoomId(messageDto.getRoomId());
                    ResignChatRoom resignChatRoom = new ResignChatRoom(findChatRoom);
                    resignChatRoomJpaRepository.save(resignChatRoom);
                    for (ChatMessage message : chatMessage) {
                        ResignChatMessage resignChatMessage = new ResignChatMessage(message);
                        resignChatMessageJpaRepository.save(resignChatMessage);
                    }
                    chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
                    chatRoomJpaRepository.deleteByRoomId(messageDto.getRoomId());
                }
            }
            chatMessageJpaRepository.deleteByRoomId(messageDto.getRoomId());
        }
        chatMessageRepository.save(messageDto); // ????????? ?????? ??????.
        ChatMessage chatMessage = new ChatMessage(messageDto, createdAt);
        chatMessageJpaRepository.save(chatMessage); // DB ??????
        // Websocket ??? ????????? ???????????? redis ??? ????????????(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
    }

    //redis??? ?????????????????? message ??? ??????
    public List<ChatMessageDto> getMessages(String roomId) {
        return chatMessageRepository.findAllMessage(roomId);
    }

    public String getFileUrl(MultipartFile file, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("???????????? ?????? ????????? ?????????.")
        );
        return s3Service.upload(file);
    }

    //???????????? ????????? ????????? ?????? ??????
    public List<UserinfoDto> getUserinfo(UserDetailsImpl userDetails, String roomId) {
        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserApiException("???????????? ?????? ????????? ?????????.")
        );
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(Long.parseLong(roomId));
        List<UserinfoDto> users = new ArrayList<>();
        for (InvitedUsers invitedUser : invitedUsers) {
            User user = invitedUser.getUser();
            users.add(new UserinfoDto(user.getNickName(), user.getProfileUrl(), user.getId()));
        }
        return users;
    }

    // ?????? ????????? ??????
    public List<FilesDto> getFiles(UserDetailsImpl userDetails, String roomId) {
        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserApiException("???????????? ?????? ????????? ?????????.")
        );
        List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);
        List<FilesDto> filesDtoList = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {

            if (chatMessage.getFileUrl() != null) {
                filesDtoList.add(new FilesDto(chatMessage.getFileUrl()));
            }
        }
        return filesDtoList;
    }

    //?????? ?????? ???????????? (????????? ?????????)
    public ResponseEntity<UserDetailDto> getUserDetails(String roomId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("???????????? ?????? ????????? ?????????!")
        );
        ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(roomId);

        if (chatRoom.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<>(new UserDetailDto(true, "?????? ?????? ?????? ??????", user, true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new UserDetailDto(true, "?????? ?????? ?????? ??????", user, false), HttpStatus.OK);
        }

    }
}

