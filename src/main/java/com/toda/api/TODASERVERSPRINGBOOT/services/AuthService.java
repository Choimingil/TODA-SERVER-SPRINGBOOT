package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.providers.RedisProvider;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component("authService")
@RequiredArgsConstructor
public class AuthService extends AbstractService implements BaseService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisProvider redisProvider;

    //1. 자체 로그인 API
    @Transactional
    public String createJwt(LoginRequest loginRequest) {
        String email = loginRequest.getId();
        String pw = loginRequest.getPw();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,pw);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfoAllDao userInfoAllDao = redisProvider.getUserInfo(loginRequest.getId());
        return tokenProvider.createToken(authentication, userInfoAllDao);
    }

    //1-3. 토큰 데이터 추출 API
    @Transactional
    public Map<String,?> decodeToken(String token) {
        Claims claims = tokenProvider.getClaims(token);
        UserInfoAllDao userInfoAllDao = redisProvider.getUserInfo(claims.getSubject());

        return DecodeTokenResponseDto.builder()
                .id(Long.parseLong(String.valueOf(claims.get("userID"))))
                .pw(userInfoAllDao.getPassword())
                .appPw(Integer.parseInt(String.valueOf(claims.get("appPassword"))))
                .build().toMap();
    }
}
