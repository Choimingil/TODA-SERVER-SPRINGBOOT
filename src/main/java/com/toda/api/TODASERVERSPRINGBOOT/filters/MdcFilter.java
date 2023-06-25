package com.toda.api.TODASERVERSPRINGBOOT.filters;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.filters.base.AbstractFilter;
import com.toda.api.TODASERVERSPRINGBOOT.filters.base.BaseFilter;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.MdcProvider;
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
public final class MdcFilter extends AbstractFilter implements BaseFilter {
    private final FilterExceptionHandler filterExceptionHandler;
    private final MdcProvider mdcProvider;

    @Override
    public void doFilterLogic(HttpServletRequest request, HttpServletResponse response) {
        logger.info("3. mdc 저장");
        mdcProvider.setMdc(request);
    }

    @Override
    public FilterExceptionHandler getFilterExceptionHandler() {
        return filterExceptionHandler;
    }
}
