package com.sparta.meeting_platform.chat.service;


import com.sparta.meeting_platform.chat.dto.*;
import com.sparta.meeting_platform.chat.model.ChatMessage;
import com.sparta.meeting_platform.chat.model.ChatRoom;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import com.sparta.meeting_platform.chat.repository.*;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.exception.UserApiException;
import com.sparta.meeting_platform.repository.UserRepository;
import com.sparta.meeting_platform.security.JwtTokenProvider;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import com.sparta.meeting_platform.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final S3Service s3Service;
    private final InvitedUsersRepository invitedUsersRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Transactional
    public void save(ChatMessageDto messageDto, String BearerToken) {
        log.info("save Message : {}", messageDto.getMessage());
        String username = jwtTokenProvider.getUserPk(BearerToken); // 토큰에서 유저 아이디 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
        );
        //date type 을 string으로 형변환시킨다.
        DateFormat dateFormat = new SimpleDateFormat("dd,MM,yyyy,HH,mm,ss", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.getTimeInMillis());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateToStr = dateFormat.format(date);
        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());
        messageDto.setEnterUserCnt(enterUserCnt);
        messageDto.setSender(user.getNickName());
        messageDto.setProfileUrl(user.getProfileUrl());
        messageDto.setCreatedAt(dateToStr);
        messageDto.setUsername(username);
        log.info("type : {}", messageDto.getType());

        //받아온 메세지의 타입이 ENTER 일때
        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
            chatRoomRepository.enterChatRoom(messageDto.getRoomId());
            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 입장하셨습니다.");
            String roomId = messageDto.getRoomId();
            //초대된 유저에 채팅방 아이디와 유저를 함께 저장한다
            InvitedUsers invitedUsers = new InvitedUsers(Long.parseLong(roomId), user);
            // 이미 그방에 초대되어 있다면 중복으로 저장을 하지 않게 한다.
            if (!invitedUsersRepository.existsByUserIdAndPostId(user.getId(), Long.parseLong(roomId))) {
                invitedUsersRepository.save(invitedUsers);
            }
            //받아온 메세지 타입이 QUIT 일때
        }else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {
            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 나가셨습니다.");
            // 들어갈때 저장했던 유저정보를 삭제해준다.
            invitedUsersRepository.deleteByUserIdAndPostId(user.getId(),Long.parseLong(messageDto.getRoomId()));
        }

        log.info("ENTER : {}", messageDto.getMessage());

        ChatRoom chatRoom = chatRoomJpaRepository.findByUsername(username);
        chatMessageRepository.save(messageDto); // 캐시에 저장 했다.
        ChatMessage chatMessage = new ChatMessage(messageDto,chatRoom);
        chatMessageJpaRepository.save(chatMessage); // DB 저장
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
    }

    //redis에 저장되어있는 message 들 출력
    public List<ChatMessageDto> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }

    public String getFileUrl(MultipartFile file, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다.")
        );
        return s3Service.upload(file);
    }

    //채팅방에 참여한 사용자 정보 조회
    public List<UserinfoDto> getUserinfo(UserDetailsImpl userDetails,String roomId) {
        userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다.")
        );
        List<InvitedUsers> invitedUsers = invitedUsersRepository.findAllByPostId(Long.parseLong(roomId));
        List<UserinfoDto> users = new ArrayList<>();

        for (InvitedUsers invitedUser : invitedUsers) {
           User user = invitedUser.getUser();
            users.add(new UserinfoDto(user.getNickName(),user.getProfileUrl(),user.getId()));
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

            if(chatMessage.getFileUrl() != null){
                filesDtoList.add(new FilesDto(chatMessage.getFileUrl()));
            }
        }
        return filesDtoList;
    }

    //유저 정보 상세조회 (채팅방 안에서)
    public ResponseEntity<UserDetailDto> getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserApiException("존재하지 않는 사용자 입니다!")
        );
        return new ResponseEntity<>(new UserDetailDto(true, "게시글 조회 성공", user), HttpStatus.OK);
    }
}

