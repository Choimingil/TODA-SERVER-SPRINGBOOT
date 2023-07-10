package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.plugins.RedisPlugin;
import com.toda.api.TODASERVERSPRINGBOOT.providers.AuthenticationProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UriProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TokenInterceptor extends AbstractInterceptor implements BaseInterceptor, RedisPlugin {
    private final UriProvider uriProvider;
    private final TokenProvider tokenProvider;
    private final AuthenticationProvider authenticationProvider;
    private final AuthRepository authRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;
    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) throws InvalidProtocolBufferException {
        if(!uriProvider.isValidPass(request)){
            String token = tokenProvider.getToken(request);
            Claims claims = tokenProvider.getClaims(token);
            UserInfoAllDao userInfoAllDao = getUserInfo(claims.getSubject());
            authenticationProvider.setSecurityContextHolder(token, claims);
        }
        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }

    @Override
    public ValueOperations<String, byte[]> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    @Override
    public AuthRepository getRepository() {
        return authRepository;
    }
}
