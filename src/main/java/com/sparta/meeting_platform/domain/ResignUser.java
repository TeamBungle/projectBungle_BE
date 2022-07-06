package com.sparta.meeting_platform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor()
@Setter
@Entity
public class ResignUser {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @CreatedDate
    private LocalDateTime checkTime;

    @Column(nullable = false)
    private int mannerTemp;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private  UserRoleEnum role;

    public ResignUser(User user){
        this.username = user.getUsername();
        this.checkTime = LocalDateTime.now();
        this.mannerTemp =user.getMannerTemp();
        this.role = UserRoleEnum.RESIGN_USER;
    }

}
