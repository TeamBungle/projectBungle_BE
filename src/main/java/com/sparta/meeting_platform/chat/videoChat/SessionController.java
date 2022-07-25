package com.sparta.meeting_platform.chat.videoChat;

import com.sparta.meeting_platform.dto.FinalResponseDto;
import com.sparta.meeting_platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class SessionController {

    private final SessionService sessionService;

    // 화상 채팅방 입장
    @PostMapping(value = "/{postId}")
    public ResponseEntity<FinalResponseDto<?>> enterRoom(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return sessionService.enterRoom(postId, userDetails.getUser().getId());

    }

    // 화상 채팅방 퇴장
    @PostMapping(value = "/leave")
    public ResponseEntity<FinalResponseDto<?>> removeUser(
            @RequestBody VideoChatLeaveRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws ParseException {

        return sessionService.leaveRoom(requestDto, userDetails.getUser().getId());
    }

}

//https://github.com/hanghae99-6/Back-End/blob/da400758aac35fd055772d7172f5ac6df7e8e175/src/main/java/com/sparta/demo/service/SessionService.java#L210