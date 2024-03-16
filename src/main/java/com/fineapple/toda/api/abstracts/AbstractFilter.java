package com.fineapple.toda.api.abstracts;

import com.fineapple.toda.api.abstracts.delegates.DelegateUri;
import com.fineapple.toda.api.abstracts.interfaces.BaseFilter;
import com.fineapple.toda.api.handlers.FilterExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

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
