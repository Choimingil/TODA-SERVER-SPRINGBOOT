package com.toda.api.TODASERVERSPRINGBOOT.utils.filters;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.MdcProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class MdcFilter extends OncePerRequestFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    private final MdcProvider mdcProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 3. 요청 정보 mdc에 저장
            logger.info("3. mdc 저장");
            mdcProvider.setMdc(request);

            filterChain.doFilter(request,response);
        }
        catch(ValidationException e){
            logger.error(e.getMessage());
            filterExceptionHandler.setErrorResponse(e.getCode(),e.getMessage(),response);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            filterExceptionHandler.sendErrorToSlack(request,response,e);
        }
    }
}
