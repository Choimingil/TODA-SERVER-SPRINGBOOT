package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.providers.FcmTokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UserProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class RedisInterceptor extends AbstractInterceptor implements BaseInterceptor {
    private final FcmTokenProvider fcmTokenProvider;
    private final TokenProvider tokenProvider;
    private final UserProvider userProvider;

    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(tokenProvider.isExistHeader(request) && tokenProvider.isValidHeader(request)){
            Claims claims = tokenProvider.getClaims(request);
            UserData user = userProvider.getUserInfo(claims.getSubject());
            fcmTokenProvider.checkFcmExist(user.getUserID());
            if(!user.getEmail().equals(claims.getSubject())) throw new WrongArgException(WrongArgException.of.WRONG_TOKEN_DATA_EXCEPTION);
        }
        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }
}
