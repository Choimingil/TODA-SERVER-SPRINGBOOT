package com.toda.api.TODASERVERSPRINGBOOT.interceptors;

import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.AbstractInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.interceptors.base.BaseInterceptor;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.UriProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class StickerInterceptor extends AbstractInterceptor implements BaseInterceptor {
    private final UriProvider uriProvider;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Override
    public boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!uriProvider.isValidPass(request)){
            String token = tokenProvider.getToken(request);
            long userID = tokenProvider.getUserID(token);
            userService.setSticker(userID);
        }
        return true;
    }

    @Override
    public void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler) {

    }
}
