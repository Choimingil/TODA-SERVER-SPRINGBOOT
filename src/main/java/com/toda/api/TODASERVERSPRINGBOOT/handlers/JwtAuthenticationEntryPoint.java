package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.JwtAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 401 에러, 즉 토큰 인증이 되지 않을 경우 예외 처리
@Component
@RequiredArgsConstructor
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        filterExceptionHandler.getResponse(request, response, new JwtAuthenticationException(authException.getMessage()));
    }
}

