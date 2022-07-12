package com.sparta.meeting_platform.chat.model;

import com.sparta.meeting_platform.chat.dto.UserDto;
import com.sparta.meeting_platform.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {
    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private String roomId;
    private String username;

    public static ChatRoom create(Post post, UserDto userDto) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(post.getId());
        chatRoom.username=userDto.getUsername();
        return chatRoom;
    }
}
