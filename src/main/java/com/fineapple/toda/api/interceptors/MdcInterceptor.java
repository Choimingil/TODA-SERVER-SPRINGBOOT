package com.fineapple.toda.api.interceptors;

import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.abstracts.delegates.DelegateUri;
import com.fineapple.toda.api.exceptions.WrongAccessException;
import com.fineapple.toda.api.abstracts.AbstractInterceptor;
import com.fineapple.toda.api.abstracts.delegates.DelegateMdc;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public final class MdcInterceptor extends AbstractInterceptor implements HandlerInterceptor {
    private final DelegateMdc delegateMdc;

    public MdcInterceptor(DelegateJwt delegateJwt, DelegateUri delegateUri, DelegateMdc delegateMdc) {
        super(delegateJwt, delegateUri);
        this.delegateMdc = delegateMdc;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler){
        delegateMdc.setLogSet(request);
        if (!delegateMdc.isMdcSet())
            throw new WrongAccessException(WrongAccessException.of.MDC_SETTING_EXCEPTION);

        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable ModelAndView modelAndView){
        MDC.clear();
    }
}
