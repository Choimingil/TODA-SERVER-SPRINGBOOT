package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.UriProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class UriFilter extends OncePerRequestFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    private final UriProvider uriProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try{
            // 1. URI가 유효한지, 각 URI의 request값이 무엇인지 체크
            logger.info("1. URI 유효성 검사");
            String uri = uriProvider.getURI(request);
            uriProvider.checkURI(uri);

            // Body, PathVariable, QueryString : 각 Model 또는 Controller에서 벨리데이션 진행

            filterChain.doFilter(request,response);
        }
        catch (ValidationException e){
            logger.error(e.getMessage());
            filterExceptionHandler.setErrorResponse(e.getCode(),e.getMessage(),response);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            filterExceptionHandler.sendErrorToSlack(request,response,e);
        }
    }
}
