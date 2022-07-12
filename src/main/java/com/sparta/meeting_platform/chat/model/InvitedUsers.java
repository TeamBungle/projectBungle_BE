package com.sparta.meeting_platform.chat.model;

import com.sparta.meeting_platform.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InvitedUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String roomId;

    @JoinColumn(name="USER_ID")
    @ManyToOne
    private User user;

    public InvitedUsers(String roomId, User user) {
        this.roomId = roomId;
        this.user = user;
    }
}
