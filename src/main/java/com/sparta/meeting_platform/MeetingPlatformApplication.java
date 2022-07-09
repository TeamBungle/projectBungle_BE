package com.sparta.meeting_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class MeetingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingPlatformApplication.class, args);
    }

}
