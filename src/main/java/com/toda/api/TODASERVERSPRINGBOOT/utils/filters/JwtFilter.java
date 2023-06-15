package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.utils.interfaces.ExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.UriProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter implements ExceptionHandler {
    // Singleton Pattern
    private static JwtFilter jwtFilter = null;
    public static JwtFilter getInstance(){
        if(jwtFilter == null){
            jwtFilter = new JwtFilter();
        }
        return jwtFilter;
    }

    // Jwt 유효성 검사 필터
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        try{
            // 2. 헤더의 토큰이 존재하는지 체크
            logger.info("2. 토큰 유효성 검사");
            UriProvider uriProvider = UriProvider.getInstance();
            TokenProvider tokenProvider = TokenProvider.getInstance();

            // 토큰이 필요 없는 API는 패스
            String uri = uriProvider.getURI(request);
            if(!uriProvider.isValidationPass(uri)){
                String jwt = tokenProvider.resolveToken(request, TokenProvider.HEADER_NAME);

                // 토큰 유효성 검증 후 SecurityContext에 저장
                Claims claims = tokenProvider.validateToken(jwt);
                Authentication authentication = tokenProvider.getAuthentication(jwt, claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request,response);
        }
        catch(ValidationException e){
            logger.error(e.getMessage());
            setErrorResponse(e.getCode(),e.getMessage(),response);
        }
    }

    @Override
    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse.Builder(code,message).build();
        String json = new ObjectMapper().writeValueAsString(errorResponse.info);
        response.getWriter().write(json);
    }
}