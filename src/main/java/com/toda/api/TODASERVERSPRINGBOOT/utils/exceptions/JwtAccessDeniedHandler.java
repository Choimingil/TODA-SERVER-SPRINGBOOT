package com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DefaultResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces.ExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 403 에러, 즉 인가 과정에서 현재 유저가 수행 권한이 존재하지 않을 경우 예외 처리
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler, ExceptionHandler {
    // Singleton Pattern
    private static JwtAccessDeniedHandler jwtAccessDeniedHandler = null;
    public static JwtAccessDeniedHandler getInstance(){
        if(jwtAccessDeniedHandler == null){
            jwtAccessDeniedHandler = new JwtAccessDeniedHandler();
        }
        return jwtAccessDeniedHandler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setErrorResponse(403,"현재 API의 사용 권한이 존재하지 않습니다.", response);
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