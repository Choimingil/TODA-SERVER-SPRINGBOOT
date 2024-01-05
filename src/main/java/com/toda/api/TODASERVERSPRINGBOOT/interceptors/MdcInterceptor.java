package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateMdc;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public final class MdcInterceptor extends AbstractInterceptor implements HandlerInterceptor {
    private final DelegateMdc delegateMdc;

    public MdcInterceptor(DelegateJwt delegateJwt, DelegateMdc delegateMdc) {
        super(delegateJwt);
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
