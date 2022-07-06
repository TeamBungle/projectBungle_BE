package com.sparta.meeting_platform.service;

import com.sparta.meeting_platform.domain.ResignUser;
import com.sparta.meeting_platform.domain.User;
import com.sparta.meeting_platform.domain.UserRoleEnum;
import com.sparta.meeting_platform.repository.ResignUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserRoleCheckService {

    private final ResignUserRepository resignUserRepository;

    public void userRoleCheck(User user){
        switch (user.getRole()){
            case NEW_USER: throw new IllegalArgumentException("이메일 인증이 필요합니다.");
            case STOP_USER:
                if (user.getCheckTime().plusDays(2).isAfter(LocalDateTime.now())){
                    throw new IllegalArgumentException("이용 정지된 회원 입니다.");
                } else {
                    user.setRole(UserRoleEnum.USER);
                    user.setMannerTemp(user.getMannerTemp()+25);
                    break;
                }
        }
    }

    @Transactional
    public int userResignCheck(String username){
        Optional<ResignUser> resignUser = resignUserRepository.findByUsername(username);
        int mannerTemp = 50;
        if(resignUser.isPresent()){
            LocalDateTime timeCheck = resignUser.get().getCheckTime().plusDays(2);
            if(timeCheck.isAfter(LocalDateTime.now())){
                throw new IllegalArgumentException("탈퇴 후 2일 이내에는 재 가입이 불가합니다.");
            } else {
                mannerTemp = resignUser.get().getMannerTemp() + 25;
                resignUserRepository.deleteById(resignUser.get().getId());
            }
        }
        return mannerTemp;
    }

}
