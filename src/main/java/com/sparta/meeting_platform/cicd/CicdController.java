package com.sparta.meeting_platform.cicd;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CicdController {

    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }

}
