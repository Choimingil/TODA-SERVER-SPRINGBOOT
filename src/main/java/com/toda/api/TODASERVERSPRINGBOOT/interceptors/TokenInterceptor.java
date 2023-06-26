package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.providers.AuthenticationProvider;
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
    private final AuthenticationProvider authenticationProvider;
    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!uriProvider.isValidPass(request)){
            String token = tokenProvider.getToken(request);
            Claims claims = tokenProvider.getClaims(token);
            if(!authenticationProvider.isExistRedis(claims)){
                if(!authenticationProvider.isEqualWithDB(claims))
                    throw new ValidationException("WRONG_TOKEN_DATA_EXCEPTION");
            }

            authenticationProvider.setSecurityContextHolder(token, claims);
        }

        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }
}
