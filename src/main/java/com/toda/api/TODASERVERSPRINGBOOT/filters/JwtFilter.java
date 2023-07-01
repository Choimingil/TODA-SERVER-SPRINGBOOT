package com.toda.api.TODASERVERSPRINGBOOT.filters;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.filters.base.AbstractFilter;
import com.toda.api.TODASERVERSPRINGBOOT.filters.base.BaseFilter;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UriProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class JwtFilter extends AbstractFilter implements BaseFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    private final UriProvider uriProvider;
    private final TokenProvider tokenProvider;

    @Override
    public void doFilterLogic(HttpServletRequest request, HttpServletResponse response) throws IOException{
        logger.info("2. 토큰 유효성 검사");

        // 토큰이 필요 없는 API는 패스
        if(!uriProvider.isValidPass(request)){
            String token = tokenProvider.getToken(request);
            if(!tokenProvider.isExistHeader(token)) throw new NoArgException(NoArgException.of.NO_HEADER_EXCEPTION);
            if(!tokenProvider.isValidHeader(token)) throw new WrongArgException(WrongArgException.of.WRONG_HEADER_EXCEPTION);
        }
    }

    @Override
    public FilterExceptionHandler getFilterExceptionHandler() {
        return filterExceptionHandler;
    }
}