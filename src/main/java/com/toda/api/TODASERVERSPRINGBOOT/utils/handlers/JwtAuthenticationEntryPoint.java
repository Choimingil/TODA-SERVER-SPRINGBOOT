package com.toda.api.TODASERVERSPRINGBOOT.utils.handlers;

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
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // Singleton Pattern
    private static JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = null;
    public static JwtAuthenticationEntryPoint getInstance(){
        if(jwtAuthenticationEntryPoint == null){
            jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
        }
        return jwtAuthenticationEntryPoint;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        FilterExceptionHandler.getInstance().setErrorResponse(403,"현재 API의 사용 권한이 존재하지 않습니다.", response);
    }
}

