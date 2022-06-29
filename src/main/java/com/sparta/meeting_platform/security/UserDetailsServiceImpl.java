package com.sparta.meeting_platform.security;

import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

// 로그인 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행
// http://localhost:8080/login 요청이 들어올때 동작한다 그이유는 스프링시큐리티 기본 로그인 주소이다
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    //리턴값은 Security Session 안에=> Authentication 안에=> UserDetails(이값으로 리턴)
    //loadUserByUsername가 다 해준다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new NoSuchElementException("등록되지 않은 유저 입니다.")
        );

        return new UserDetailsImpl(userEntity);
    }
}
