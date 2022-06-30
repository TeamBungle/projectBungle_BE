package com.sparta.meeting_platform.security;



import com.sparta.meeting_platform.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// 시큐리티가 어떤 주소로 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인 진행이 완료 되면 시큐리티 session을 만들어 줍니다.(Security ContestHolder)
//오브젝트 타입 => Authentication 타입객체
// Authentication 안에 User정보가 있어야 됨.
// User 오브젝트타입 => UserDetails 타입 객체

//Security Session 안에=> Authentication 안에=> UserDetails(UserDetailsImpl)

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //해당유저의 권한을 리턴한다
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // isAccountNonExpired 만료되었니??
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 이 계정 잠겼니?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 이 계정의 비밀번호가 너무 오래되었니?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 이 계정이 활성화 되어있니? (휴먼계정 설정할때)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
