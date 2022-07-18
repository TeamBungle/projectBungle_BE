package com.sparta.meeting_platform.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ResignChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String roomId;

    private String username;

    public ResignChatRoom(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.username = chatRoom.getUsername();
    }
}
