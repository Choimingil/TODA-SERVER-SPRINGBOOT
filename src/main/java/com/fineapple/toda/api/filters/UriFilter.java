package com.fineapple.toda.api.filters;

import com.fineapple.toda.api.abstracts.delegates.DelegateUri;
import com.fineapple.toda.api.exceptions.NoArgException;
import com.fineapple.toda.api.handlers.FilterExceptionHandler;
import com.fineapple.toda.api.abstracts.AbstractFilter;
import com.fineapple.toda.api.abstracts.interfaces.BaseFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public final class UriFilter extends AbstractFilter implements BaseFilter {
    public UriFilter(FilterExceptionHandler filterExceptionHandler, DelegateUri delegateUri) {
        super(filterExceptionHandler, delegateUri);
    }

    @Override
    public void doFilterLogic(HttpServletRequest request, HttpServletResponse response) {
        logger.info("1. URI 유효성 검사");
        if(!isValidUri(request)) throw new NoArgException(NoArgException.of.NO_URI_EXCEPTION);
    }
}
