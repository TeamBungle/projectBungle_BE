package com.sparta.meeting_platform.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "rewind";
    public final HttpServletResponse response;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public void createToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();
        System.out.println(secretKey);
        String jwtToken= Jwts.builder()
                .setClaims(claims)//정보저장
                .setIssuedAt(now)//토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + 24*60*60*1000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)//사용할 암호화 알고리즘
                //signature에 들어갈 secret값 세팅
                .compact();

        response.addHeader("Authorization","Bearer " +jwtToken);
    }


    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new JwtException("JWT 인증에 실패하셨습니다");
        }
    }


    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails
                = userDetailsService.loadUserByUsername
                        (Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
