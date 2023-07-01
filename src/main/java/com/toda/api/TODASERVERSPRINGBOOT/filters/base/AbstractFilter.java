package com.toda.api.TODASERVERSPRINGBOOT.filters.base;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class AbstractFilter extends OncePerRequestFilter implements BaseFilter {
    /**
     * 실제 필터 로직 수행하는 메소드 구현
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try{
            doFilterLogic(request,response);
            filterChain.doFilter(request,response);
        }
        catch (Exception e){
            getFilterExceptionHandler().getResponse(request, response, e);
        }
    }
}
