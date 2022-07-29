package com.sparta.meeting_platform.domain;

import com.sparta.meeting_platform.dto.UserDto.ProfileRequestDto;
import com.sparta.meeting_platform.dto.UserDto.SignupRequestDto;
import com.sparta.meeting_platform.chat.model.InvitedUsers;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor()
@Setter
@Entity(name = "userinfo")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickName;

    @Column
    private String profileUrl;

    @Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String naverId;

    @CreatedDate
    private LocalDateTime checkTime;

    @Column(nullable = false)
    private int mannerTemp;

    @Column(nullable = false)
    private Boolean isOwner = false;

    @Column
    private String intro;

    @Column
    private int bungCount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private  UserRoleEnum role;

//    @OneToMany(cascade = CascadeType.REMOVE)
//    private List<Opinion> opinionList;
//
//    @OneToMany
//    private List<Like> likeList;
//
//    @OneToMany(cascade = CascadeType.REMOVE)
//    private List<Post> postList;
//
//    @OneToMany(cascade = CascadeType.REMOVE)
//    private List<InvitedUsers> invitedUsersList;

    public User(SignupRequestDto requestDto, int mannerTemp) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.checkTime = LocalDateTime.now();
        this.mannerTemp = mannerTemp;
        this.nickName = "벙글" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        this.profileUrl = "https://user-images.githubusercontent.com/87007109/178628349-839deab9-5c31-49e5-beb5-3d6b868df343.jpg";
        this.role = UserRoleEnum.NEW_USER;
    }

    @Builder
    public User(String username, String password, String nickName, String profileUrl, Long kakaoId, String googleId,
                String naverId, LocalDateTime createdAt, int mannerTemp, Boolean isOwner, String intro, int bungCount,
                UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.kakaoId = kakaoId;
        this.googleId = googleId;
        this.naverId = naverId;
        this.checkTime = createdAt;
        this.mannerTemp = mannerTemp;
        this.isOwner = isOwner;
        this.intro = intro;
        this.bungCount = bungCount;
        this.role = role;
    }

    public void setReport(){
        this.mannerTemp -= 5;
        if (this.mannerTemp < 26){
            this.role = UserRoleEnum.STOP_USER;
            this.checkTime = LocalDateTime.now();
        }
    }

    public void updateProfile(ProfileRequestDto requestDto, String profileUrl){
        this.nickName = requestDto.getNickName();
        this.profileUrl = profileUrl;
        this.intro = requestDto.getIntro();
    }

    public void updateMannerTempAndBungCount() {
        this.mannerTemp += 5;
        this.bungCount += 1;
    }
}
