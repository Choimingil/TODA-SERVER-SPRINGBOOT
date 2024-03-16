package com.fineapple.toda.api.filters;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.abstracts.delegates.DelegateUri;
import com.fineapple.toda.api.exceptions.NoArgException;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.abstracts.AbstractFilter;
import com.fineapple.toda.api.abstracts.interfaces.BaseFilter;
import com.fineapple.toda.api.handlers.FilterExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public final class JwtFilter extends AbstractFilter implements BaseFilter {
    private final DelegateJwt delegateJwt;

    public JwtFilter(FilterExceptionHandler filterExceptionHandler, DelegateUri delegateUri, DelegateJwt delegateJwt) {
        super(filterExceptionHandler, delegateUri);
        this.delegateJwt = delegateJwt;
    }

    @Override
    public void doFilterLogic(HttpServletRequest request, HttpServletResponse response) {
        logger.info("2. 토큰 유효성 검사");

        if(!isValidPass(request)){
            if(!delegateJwt.isExistHeader(request)) throw new NoArgException(NoArgException.of.NO_HEADER_EXCEPTION);
            else if(!delegateJwt.isValidHeader(request)) throw new WrongArgException(WrongArgException.of.WRONG_HEADER_EXCEPTION);
            delegateJwt.setSecurityContextHolder(request);
        }
    }
}