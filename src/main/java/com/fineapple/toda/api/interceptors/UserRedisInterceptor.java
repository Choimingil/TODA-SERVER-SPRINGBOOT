package com.fineapple.toda.api.interceptors;

import com.fineapple.toda.api.abstracts.AbstractInterceptor;
import com.fineapple.toda.api.abstracts.delegates.DelegateJwt;
import com.fineapple.toda.api.abstracts.delegates.DelegateUri;
import com.fineapple.toda.api.abstracts.delegates.DelegateUserAuth;
import com.fineapple.toda.api.exceptions.WrongArgException;
import com.fineapple.toda.api.entities.mappings.UserDetail;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public final class UserRedisInterceptor extends AbstractInterceptor implements HandlerInterceptor {
    private final DelegateUserAuth delegateUserAuth;

    public UserRedisInterceptor(DelegateJwt delegateJwt, DelegateUri delegateUri, DelegateUserAuth delegateUserAuth) {
        super(delegateJwt, delegateUri);
        this.delegateUserAuth = delegateUserAuth;
    }

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) throws Exception {
        if(haveValidHeader(request)){
            String email = getEmailWithDecodeToken(request);
            UserDetail userDetail = delegateUserAuth.getUserInfo(email);
            if(!userDetail.getUser().getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_TOKEN_DATA_EXCEPTION);
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
