package org.example.expert.domain.common.aop;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.config.JwtFilter;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final JwtUtil jwtUtil;

    @Pointcut("@annotation(org.example.expert.domain.common.annotation.Logging)")
    private void LoggingAnnotation() {

    }

    @Around("LoggingAnnotation()")
    public Object adviceLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            log.info(String.valueOf(request.getAttribute("userId")));
            log.info(String.valueOf(LocalDateTime.now()));
            log.info(String.valueOf(request.getRequestURL()));
        }
    }

}
