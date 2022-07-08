package com.sparta.meeting_platform.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.meeting_platform.dto.FinalResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        FinalResponseDto<Object> responseDto = new FinalResponseDto<>(true, "로그아웃 성공");
        String result =mapper.writeValueAsString(responseDto);
        response.getWriter().write(result);

    }
}
