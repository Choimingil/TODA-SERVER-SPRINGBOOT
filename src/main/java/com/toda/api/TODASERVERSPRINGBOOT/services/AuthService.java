package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
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
    private final UserImageRepository userImageRepository;
    private final TokenProvider tokenProvider;
    private final RedisProvider redisProvider;

    public String createJwt(LoginRequest loginRequest) {
        String email = loginRequest.getId();
        String pw = loginRequest.getPw();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = redisProvider.getUserInfo(loginRequest.getId());
        UserImage userImage = userImageRepository.findByUserIDAndStatusNot(user.getUserID(), 0);
        return tokenProvider.createToken(authentication, user, userImage.getUrl());
    }

    public Map<String,?> decodeToken(String token) {
        User decodedToken = tokenProvider.getUserInfo(token);
        User user = redisProvider.getUserInfo(decodedToken.getEmail());

        Map<String,Object> map = new HashMap<>();
        map.put("id", user.getUserID());
        map.put("pw", user.getPassword());
        map.put("appPw", user.getAppPassword());
        return map;
    }
}
