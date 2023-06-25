package com.toda.api.TODASERVERSPRINGBOOT.filters;

import com.toda.api.TODASERVERSPRINGBOOT.filters.base.AbstractFilter;
import com.toda.api.TODASERVERSPRINGBOOT.filters.base.BaseFilter;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UriProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class JwtFilter extends AbstractFilter implements BaseFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    private final UriProvider uriProvider;
    private final TokenProvider tokenProvider;

    @Override
    public void doFilterLogic(HttpServletRequest request, HttpServletResponse response) {
        // 2. 헤더의 토큰이 존재하는지 체크
        logger.info("2. 토큰 유효성 검사");

        // 토큰이 필요 없는 API는 패스
        String uri = uriProvider.getURI(request);
        if(!uriProvider.isValidationPass(uri)){
            String jwt = tokenProvider.resolveToken(request, TokenProvider.HEADER_NAME);

            // 토큰 유효성 검증 후 SecurityContext에 저장
            Claims claims = tokenProvider.getAuthenticationClaims(jwt);
            Authentication authentication = tokenProvider.getAuthentication(jwt, claims);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @Override
    public FilterExceptionHandler getFilterExceptionHandler() {
        return filterExceptionHandler;
    }
}