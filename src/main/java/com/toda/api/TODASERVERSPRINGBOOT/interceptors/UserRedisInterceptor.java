package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJwt;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateUserAuth;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public final class UserRedisInterceptor extends AbstractInterceptor implements HandlerInterceptor {
    private final DelegateUserAuth delegateUserAuth;

    public UserRedisInterceptor(DelegateJwt delegateJwt, DelegateUserAuth delegateUserAuth) {
        super(delegateJwt);
        this.delegateUserAuth = delegateUserAuth;
    }

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        if(haveValidHeader(request)){
            String email = getSubject(request);
            UserData user = delegateUserAuth.getUserInfo(email);
            if(!user.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_TOKEN_DATA_EXCEPTION);
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
