package com.sparta.meeting_platform.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ResignChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private String roomId;
    @Column
    private String username;

    public ResignChatRoom(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.username = chatRoom.getUsername();
    }
}
