package com.sparta.meeting_platform.cicd;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CICDController {
    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }
}