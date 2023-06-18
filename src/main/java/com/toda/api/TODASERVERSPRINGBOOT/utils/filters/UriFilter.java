package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.UriProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UriFilter extends OncePerRequestFilter {
    // Singleton Pattern
    private UriFilter(){}
    private static UriFilter uriFilter = null;
    public static UriFilter getInstance(){
        if(uriFilter == null){
            uriFilter = new UriFilter();
        }
        return uriFilter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 1. URI가 유효한지, 각 URI의 request값이 무엇인지 체크
            logger.info("1. URI 유효성 검사");
            UriProvider uriProvider = UriProvider.getInstance();

            String uri = uriProvider.getURI(request);
            uriProvider.checkURI(uri);

            // Body, PathVariable, QueryString : 각 Model 또는 Controller에서 벨리데이션 진행

            filterChain.doFilter(request,response);
        }
        catch (ValidationException e){
            logger.error(e.getMessage());
            FilterExceptionHandler.getInstance().setErrorResponse(e.getCode(),e.getMessage(),response);
        }

    }
}
