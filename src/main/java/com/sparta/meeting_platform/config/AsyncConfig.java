package com.sparta.meeting_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    @Bean(name = "mailExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        기본 실행 대기하는 스레드 개수
        executor.setCorePoolSize(5);
//        동시에 실행되는 최대 스레드 개수
        executor.setMaxPoolSize(10);
//        MaxPoolSize가 넘어가는 스레드 요청 시 Queue에 저장하는데, 최대 Queue에 저장 가능한 개수
        executor.setQueueCapacity(10);
//        생선되는 스레드의 접두사
        executor.setThreadNamePrefix("MailExecutor-");
        executor.initialize();
        return executor;
    }

}
