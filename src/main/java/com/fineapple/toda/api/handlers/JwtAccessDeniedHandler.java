package com.fineapple.toda.api.handlers;

import com.fineapple.toda.api.exceptions.JwtAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

// 403 에러, 즉 인가 과정에서 현재 유저가 수행 권한이 존재하지 않을 경우 예외 처리
@Component
@RequiredArgsConstructor
public final class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        try {
            filterExceptionHandler.getResponse(request, response, new JwtAccessDeniedException(accessDeniedException.getMessage()));
        }
        catch (Exception e){
            return;
        }
    }
}