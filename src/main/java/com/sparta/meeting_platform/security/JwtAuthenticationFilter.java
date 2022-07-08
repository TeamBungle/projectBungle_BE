package com.sparta.meeting_platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String apiPath = ((HttpServletRequest) request).getServletPath();

        if (apiPath.equals("/user/login") || apiPath.equals("/user/signup") || apiPath.equals("/user/duplicate/username")
                || apiPath.equals("/user/signin/kakao") || apiPath.equals("/user/signin/google")
                || apiPath.equals("/user/signin/naver") || apiPath.equals("/confirmEmail") || apiPath.equals("/confirmEmail2")
                || apiPath.equals("/ws/chat/info?t=1657279976925") || apiPath.equals("GET,/ws/chat") || apiPath.equals("/ws/chat/info")
                || apiPath.equals("/ws/chat/334/pparycob/websocket"))
          {
            chain.doFilter(request, response); // 그냥 필터 타고 넘어가라
              System.out.println("path적용");
        } else {
            // 헤더에서 jwt 토큰 받아옴
            String token = ((HttpServletRequest) request).getHeader("Authorization");
            if (token == null) {
                System.out.println("로그인이 필요합니다.");
                throw new JwtException("로그인이 필요합니다.");
//                token = "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZW9uZ2h5ZW9udWs5OEBnbWFpbC" +
//                        "5jb20iLCJpYXQiOjE2NTcyMDIzMTksImV4cCI6MTY1NzI4ODcxOX0.T27IveZSxHdPXTAsdqgXruU3MQT6bbPY2Xf3CWakhtM";
//                System.out.println(token + "= 토큰 생성");
            }
            String jwtToken = token.replace("Bearer ", "");
            // 유효한 토큰인지 확인
            if (jwtTokenProvider.validateToken(jwtToken)) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아와서 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            }
        }
    }
}

