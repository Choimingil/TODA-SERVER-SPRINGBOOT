package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.providers.MdcProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class MdcInterceptor extends AbstractInterceptor implements BaseInterceptor {
    private final MdcProvider mdcProvider;

    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        mdcProvider.setMdc(request);
        if(!mdcProvider.isMdcSet())
            throw new ValidationException("MDC_SETTING_EXCEPTION");

        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        mdcProvider.removeMdc();
    }
}
