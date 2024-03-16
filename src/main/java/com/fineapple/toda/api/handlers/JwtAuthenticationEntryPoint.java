package com.fineapple.toda.api.handlers;

import com.fineapple.toda.api.exceptions.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 401 에러, 즉 토큰 인증이 되지 않을 경우 예외 처리
@Component
@RequiredArgsConstructor
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        filterExceptionHandler.getResponse(request, response, new JwtAuthenticationException(authException.getMessage()));
    }
}

