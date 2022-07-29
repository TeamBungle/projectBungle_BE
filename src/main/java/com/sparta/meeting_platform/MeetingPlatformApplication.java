package com.sparta.meeting_platform;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableEncryptableProperties
public class MeetingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingPlatformApplication.class, args);
    }

}
