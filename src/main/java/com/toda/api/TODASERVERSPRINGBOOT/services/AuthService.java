package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserImage;
import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserImageRepository;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.LoginRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component("authService")
@RequiredArgsConstructor
public class AuthService extends AbstractService implements BaseService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisProvider redisProvider;

    public String createJwt(String email, String pw) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserData userData = redisProvider.getUserInfo(email);
        return tokenProvider.createToken(authentication, userData);
    }

    public Map<String,?> decodeToken(String token) {
        UserData userData = redisProvider.getUserInfo(token);

        Map<String,Object> map = new HashMap<>();
        map.put("id", userData.getUserID());
        map.put("pw", userData.getPassword());
        map.put("appPw", userData.getAppPassword());
        return map;
    }
}
