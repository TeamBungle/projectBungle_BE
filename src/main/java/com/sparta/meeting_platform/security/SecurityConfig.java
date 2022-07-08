package com.sparta.meeting_platform.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true) // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다는 뜻, 활성화 한다 //(debug = true)지금사용하는 필터 종류를 보여준다
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtExceptionFilter jwtExceptionFilter;


    //Bean = 해당 메서드의 리턴되는 오브젝트를 IoC로 등록 해준다
    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .csrf().disable()
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .cors()
                .configurationSource(corsConfigurationSource());


        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable() // 폼로그인을 사용안하겠다.
                .httpBasic().disable(); //http헤더에 Anthorization에 아이디와 패스워드를 달고 요청하는것을 사용안하겠다.

//        http.addFilterBefore(new MyFilter1(), SecurityContextPersistenceFilter.class); //MyFilter1가 (스프링시큐리티 필터중 하나)SecurityContextPersistenceFilter보다 먼저 실행 되라라
//        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()));



        http.authorizeRequests()
//            .antMatchers("/user/**").authenticated() //("/user/**") 일때는 권한 필요
//            .antMatchers("/adimin/**").access("hasRole('ROLE_ADMIN')") //("/adimin/**")일때는 권한과 엑세스도 필요
                .antMatchers("/ws/chat/**").permitAll()
                .anyRequest().permitAll() //나머지 요청은 토큰말고 세션 id 다 허용
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);





    }


    @Bean
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // addAllowedOrigin = 모든 ip의 대한 응답을 허용하겠다.
//        configuration.addAllowedOrigin("http://127.0.0.1:3000");
//        configuration.addAllowedOrigin("http://cloneslackweek7.s3-website.ap-northeast-2.amazonaws.com");
        configuration.addAllowedMethod("*"); // 모든 get, pust, put, delete, patch 요청을 허용하겠다.
        configuration.addAllowedHeader("*"); // 모든 헤더 응답을 허용
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true); // 내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정
        configuration.validateAllowCredentials();
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소는 이 설정(configuration)을 따라라
        return source;
    }
}
