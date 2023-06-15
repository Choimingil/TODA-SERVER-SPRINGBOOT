package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.TokenProvider;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import com.toda.api.TODASERVERSPRINGBOOT.models.requests.LoginRequestDTO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.DecodeTokenResponseDTO;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component("authService")
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthRepository authRepository;

    //1. 자체 로그인 API
    public String createJwt(LoginRequestDTO loginRequestDTO){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getId(),
                        loginRequestDTO.getPw()
                );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // CustomUserDetailsService에서 데이터 Redis로 저장해서 Redis로 접근
        UserInfoAllDAO userInfoAllDAO = authRepository.getUserInfoAll(loginRequestDTO.getId());
        return TokenProvider.getInstance().createToken(authentication, userInfoAllDAO);
    }


    //1-3. 토큰 데이터 추출 API
    public DecodeTokenResponseDTO decodeToken(String token){
        // 토큰 데이터 추출
        Key key = TokenProvider.key;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();
        long userID = Long.parseLong(String.valueOf(claims.get("userID")));
        String appPassword = (String) claims.get("appPassword");

        // Redis 에 유저 정보 존재하는지 확인
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserInfoAllDAO userInfoAllDAO = (UserInfoAllDAO) valueOperations.get(claims.getSubject());

        // 유저 정보가 없다면 DB에 접근해서 추가
        if(userInfoAllDAO == null){
            userInfoAllDAO = authRepository.getUserInfoAll(claims.getSubject());
            valueOperations.set(claims.getSubject(),userInfoAllDAO);
        }

        // 토큰 내용과 유저 정보가 같다면 값 리턴
        if(userID == userInfoAllDAO.getUserID() && appPassword.equals(userInfoAllDAO.getAppPassword())){
            DecodeTokenResponseDTO responseDTO = new DecodeTokenResponseDTO();
            responseDTO.setId(userID);
            responseDTO.setPw(userInfoAllDAO.getPassword());
            responseDTO.setAppPw(Integer.parseInt(appPassword));

            return responseDTO;
        }
        else throw new ValidationException(400,"토큰과 유저 정보가 일치하지 않습니다.");
    }

}
