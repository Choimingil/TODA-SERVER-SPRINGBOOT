package com.toda.api.TODASERVERSPRINGBOOT.filters;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateUri;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractFilter;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseFilter;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
