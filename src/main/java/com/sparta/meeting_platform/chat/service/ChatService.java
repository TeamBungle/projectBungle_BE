package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.dto.*;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.*;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.exception.UserApiException;
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

    @Transactional
    public void save(ChatMessageDto messageDto, Long userPk) {

        User user = userRepository.findById(userPk).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
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

        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
            chatRoomRepository.enterChatRoom(messageDto.getRoomId());
            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 입장하셨습니다.");
            String roomId = messageDto.getRoomId();

            List<InvitedUsers> invitedUsersList = invitedUsersRepository.findAllByPostId(Long.parseLong(roomId));
            for (InvitedUsers invitedUsers : invitedUsersList) {
                if (invitedUsers.getUser().equals(user)) {
                    invitedUsers.setReadCheck(true);
                }
            }

            // 이미 그방에 초대되어 있다면 중복으로 저장을 하지 않게 한다.
            if (!invitedUsersRepository.existsByUserIdAndPostId(user.getId(), Long.parseLong(roomId))) {
                InvitedUsers invitedUsers = new InvitedUsers(Long.parseLong(roomId), user);
                invitedUsersRepository.save(invitedUsers);
            }

        } else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {
            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 나가셨습니다.");
            invitedUsersRepository.deleteByUserIdAndPostId(user.getId(), Long.parseLong(messageDto.getRoomId()));
            ChatRoom chatRoom = chatRoomJpaRepository.findByRoomId(messageDto.getRoomId());

            if (chatRoom.getUsername().equals(user.getUsername())) {
                messageDto.setQuitOwner(true);
                messageDto.setMessage("[알림] " + "(방장) " + messageDto.getSender() + "님이 나가셨습니다. " +
                        "더 이상 대화를 할 수 없으며 채팅방을 나가면 다시 입장할 수 없습니다.");
            }
        }

        ChatRoom chatRoom = chatRoomJpaRepository.findByUsername(user.getUsername());
        chatMessageRepository.save(messageDto);
        ChatMessage chatMessage = new ChatMessage(messageDto, chatRoom, createdAt);
        chatMessageJpaRepository.save(chatMessage);

        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
    }

    // Chat 사진 파일 저장
    public String getFileUrl(MultipartFile file, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다.")
        );
        return s3Service.upload(file);
    }

    // 채팅방에 참여한 사용자 정보 조회
    public List<UserinfoDto> getUserinfo(UserDetailsImpl userDetails, String roomId) {
        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다.")
        );
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(Long.parseLong(roomId));
        List<UserinfoDto> users = new ArrayList<>();

        for (InvitedUsers invitedUser : invitedUsers) {
            User user = invitedUser.getUser();
            users.add(new UserinfoDto(user.getNickName(), user.getProfileUrl(), user.getId()));
        }
        return users;
    }

    // 파일 리스트 조회
    public List<FilesDto> getFiles(UserDetailsImpl userDetails, String roomId) {
        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다.")
        );
        List<FindChatMessageDto> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);
        List<FilesDto> filesDtoList = new ArrayList<>();

        for (FindChatMessageDto chatMessage : chatMessages) {
            if (chatMessage.getFileUrl() != null) {
                filesDtoList.add(new FilesDto(chatMessage.getFileUrl()));
            }
        }
        return filesDtoList;
    }

    // 유저 정보 상세조회 (채팅방 안에서)
    public ResponseEntity<UserDetailDto> getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다!")
        );
        return new ResponseEntity<>(new UserDetailDto(true, "게시글 조회 성공", user), HttpStatus.OK);
    }
}

