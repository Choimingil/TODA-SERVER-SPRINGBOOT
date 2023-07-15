package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UriProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TokenInterceptor extends AbstractInterceptor implements BaseInterceptor {
    private final UriProvider uriProvider;
    private final TokenProvider tokenProvider;
    private final RedisProvider redisProvider;

    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!uriProvider.isValidPass(request)){
            String token = tokenProvider.getToken(request);
            Claims claims = tokenProvider.getClaims(token);
            UserInfoAllDao userInfoAllDao = redisProvider.getUserInfo(claims.getSubject());
            if(!userInfoAllDao.isSameTokenAttributes(claims)) throw new WrongArgException(WrongArgException.of.WRONG_TOKEN_DATA_EXCEPTION);
        }
        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }
}
