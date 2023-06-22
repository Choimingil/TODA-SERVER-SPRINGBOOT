package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.MdcProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class MdcFilter extends OncePerRequestFilter {
    // Singleton Pattern
    private static MdcFilter mdcFilter = null;
    public static MdcFilter getInstance(){
        if(mdcFilter == null){
            mdcFilter = new MdcFilter();
        }
        return mdcFilter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 3. 요청 정보 mdc에 저장
            logger.info("3. mdc 저장");
            MdcProvider.getInstance().setMdc(request);

            filterChain.doFilter(request,response);
        }
        catch(ValidationException e){
            logger.error(e.getMessage());
            FilterExceptionHandler.getInstance().setErrorResponse(e.getCode(),e.getMessage(),response);
        }
    }
}
