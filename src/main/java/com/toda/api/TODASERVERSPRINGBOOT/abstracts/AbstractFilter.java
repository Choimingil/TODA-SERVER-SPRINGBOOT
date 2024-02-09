package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateUri;
import com.toda.api.TODASERVERSPRINGBOOT.enums.RegularExpressions;
import com.toda.api.TODASERVERSPRINGBOOT.enums.Uris;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseFilter;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractFilter extends OncePerRequestFilter implements BaseFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    /* Delegate Class */
    private final DelegateUri delegateUri;

    /**
     * 실제 필터 로직 수행하는 메소드 구현
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try {
            doFilterLogic(request,response);
            filterChain.doFilter(request,response);
        }
        catch(Exception e){
            filterExceptionHandler.getResponse(request,response,e);
        }
    }

    public boolean isValidUri(HttpServletRequest request) {
        return delegateUri.isValidUri(request);
    }

    public boolean isValidPass(HttpServletRequest request) {
        return delegateUri.isValidPass(request);
    }
}
