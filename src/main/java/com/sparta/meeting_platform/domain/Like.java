package com.sparta.meeting_platform.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "liketable")
public class Like {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private Boolean isLike = false;

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
        setIsLike();
    }

    public void setIsLike() {
        this.isLike = !this.isLike;
    }

}
