package com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DefaultResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces.ExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 401 에러, 즉 토큰 인증이 되지 않을 경우 예외 처리
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, ExceptionHandler {
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
        setErrorResponse(401,"토큰 인증에 실패하였습니다.", response);
    }

    @Override
    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = new ObjectMapper().writeValueAsString(new DefaultResponseDTO(code,message));
        response.getWriter().write(json);
    }
}

