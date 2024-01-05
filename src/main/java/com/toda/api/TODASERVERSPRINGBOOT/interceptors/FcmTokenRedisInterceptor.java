package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateFcmTokenAuth;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public final class FcmTokenRedisInterceptor extends AbstractInterceptor implements HandlerInterceptor {
    private final DelegateFcmTokenAuth delegateFcmTokenAuth;

    public FcmTokenRedisInterceptor(DelegateJwt delegateJwt, DelegateFcmTokenAuth delegateFcmTokenAuth) {
        super(delegateJwt);
        this.delegateFcmTokenAuth = delegateFcmTokenAuth;
    }

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        if(haveValidHeader(request)){
            UserData userData = decodeToken(getToken(request));
            delegateFcmTokenAuth.setFcmMap(userData.getUserID());
        }
        return true;
    }

    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            @Nullable ModelAndView modelAndView
    ) throws Exception {

    }
}
