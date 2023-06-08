package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces.ExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses.DefaultResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.UriProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter implements ExceptionHandler {
    public static final String HEADER_NAME = "x-access-token";
    private final UriProvider uriProvider = new UriProvider();
    private final TokenProvider tokenProvider;

    // Jwt 유효성 검사 필터
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest httpServletRequest,
            @NonNull HttpServletResponse httpServletResponse,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        try{
            // 2. 헤더의 토큰이 존재하는지 체크
            logger.info("2. 토큰 유효성 검사");

            // 토큰이 필요 없는 API는 패스
            String uri = uriProvider.getURI(httpServletRequest);
            if(!uriProvider.nonTokenUris.contains(uri)){
                String jwt = tokenProvider.resolveToken(httpServletRequest, JwtFilter.HEADER_NAME);

                // 토큰 유효성 검증 후 SecurityContext에 저장
                Claims claims = tokenProvider.validateToken(jwt);
                Authentication authentication = tokenProvider.getAuthentication(jwt, claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
        catch(ValidationException e){
            logger.error(e.getMessage());
            setErrorResponse(e.getCode(),e.getMessage(),httpServletResponse);
        }
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