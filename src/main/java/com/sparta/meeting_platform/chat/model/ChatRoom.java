package com.sparta.meeting_platform.chat.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    // 채팅방 아이디
    @Id
    private String roomId;
    // 채팅방 제목
    private String title;
    // 채팅방 정원수
    private int personnel;



}