package com.sparta.meeting_platform.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RestApiAspect {

    @Around("execution(* com.sparta.meeting_platform.controller..*(..))")
    public Object addStatus(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        log.info("START : {}",joinPoint.toString().split("controller.")[1].split("\\(")[0]);
        try {
            return joinPoint.proceed();

        } finally {
            long finish = System.currentTimeMillis();
            long timsMs = finish - start;
            log.info("END : {} {}ms",joinPoint.toLongString().split("controller.")[1].split("\\(")[0],timsMs);
        }
    }

}
