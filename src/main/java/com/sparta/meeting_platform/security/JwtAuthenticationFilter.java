package com.sparta.meeting_platform.security;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 jwt 토큰 받아옴
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        System.out.println("필터 왔냐");
        // 유효한 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            System.out.println("토큰왔냐");
            String jwtToken = token.replace("Bearer ", "");
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아와서 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}

