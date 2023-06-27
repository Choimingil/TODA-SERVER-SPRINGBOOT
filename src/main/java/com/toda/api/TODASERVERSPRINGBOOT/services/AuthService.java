package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.services.base.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.services.base.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequest;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import com.toda.api.TODASERVERSPRINGBOOT.plugins.RedisPlugin;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("authService")
@RequiredArgsConstructor
public class AuthService extends AbstractService implements BaseService, RedisPlugin {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final AuthRepository authRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    //1. 자체 로그인 API
    @Transactional
    public String createJwt(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getId(),
                        loginRequest.getPw()
                );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(!isExistRedis(loginRequest.getId()))
            throw new ValidationException("REDIS_CONNECTION_EXCEPTION");
        return tokenProvider.createToken(authentication, getRedis(loginRequest.getId(), UserInfoAllDao.class));
    }

    //1-3. 토큰 데이터 추출 API
    @Transactional
    public DecodeTokenResponseDto decodeToken(String token){
        Claims claims = tokenProvider.getClaims(token);
        if(!isExistRedis(claims)) setRedis(claims);
        UserInfoAllDao userInfoAllDao = getRedis(claims, UserInfoAllDao.class);

        // 토큰 내용과 유저 정보가 같다면 값 리턴
        if(userInfoAllDao.isSameTokenAttributes(claims)){
            return DecodeTokenResponseDto.builder()
                    .id(Long.parseLong(String.valueOf(claims.get("userID"))))
                    .pw(userInfoAllDao.getPassword())
                    .appPw(Integer.parseInt(String.valueOf(claims.get("appPassword"))))
                    .build();
        }
        else throw new ValidationException("WRONG_TOKEN_DATA_EXCEPTION");
    }

    @Override
    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    @Override
    public AuthRepository getRepository() {
        return authRepository;
    }
}
