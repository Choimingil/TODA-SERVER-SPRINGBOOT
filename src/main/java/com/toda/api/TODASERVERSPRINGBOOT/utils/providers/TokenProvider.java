package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

// 토큰의 생성, 토큰의 유효성 검증 등을 담당

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class TokenProvider implements InitializingBean {
    public static final String HEADER_NAME = "x-access-token";
    private final AuthRepository authRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private final String AUTHORITIES_KEY = "auth";
    private Key key;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInMilliseconds;

    @Override
    public void afterPropertiesSet() {
        // Bean 초기화 시 진행
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(
            Authentication authentication,
            UserInfoAllDAO userInfoAllDAO
    ){
        // authorities 설정
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 만료 시간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliseconds);

        return Jwts.builder()
                // subject : email
                .setSubject(authentication.getName())
                .claim("userID",userInfoAllDAO.getUserID())
                .claim("userCode",userInfoAllDAO.getUserCode())
                .claim("email",userInfoAllDAO.getEmail())
                .claim("userName",userInfoAllDAO.getUserName())
                .claim("appPassword",userInfoAllDAO.getAppPassword())
                .claim(AUTHORITIES_KEY,authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String resolveToken(HttpServletRequest request, String headerName){
        String token = request.getHeader(headerName);
        if(StringUtils.hasText(token)) return token;
        else throw new ValidationException(102,"헤더값이 인식되지 않습니다.");
    }

    private void validateToken(Claims claims){
        if (!isExistTokenAttributes(claims)) throw new ValidationException(103, "잘못된 헤더값입니다.");

        // Redis 에 유저 정보 존재하는지 확인
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserInfoAllDAO userInfoAllDAO = (UserInfoAllDAO) valueOperations.get(claims.getSubject());

        // 유저 정보가 없다면 DB에 접근해서 추가
        if(userInfoAllDAO == null){
            userInfoAllDAO = authRepository.getUserInfoAll(claims.getSubject());

            // 토큰과 DB의 정보가 다르다면 예외 던지기
            if(!userInfoAllDAO.isSameTokenAttributes(claims))
                throw new ValidationException(103,"토큰과 유저 정보가 일치하지 않습니다.");

            // 같다면 Redis에 저장
            valueOperations.set(claims.getSubject(),userInfoAllDAO);
        }
    }

    private boolean isExistTokenAttributes(Claims claims){
        return claims.get("userID") != null &&
                claims.get("userCode") != null &&
                claims.get("email") != null &&
                claims.get("userName") != null &&
                claims.get("appPassword") != null;
    }

    public Claims getAuthenticationClaims(String token){
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        if(!jwtParser.isSigned(token)) throw new ValidationException(103, "잘못된 헤더값입니다.");

        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        validateToken(claims);
        return claims;
    }

    // 토큰에 담겨있는 정보를 이용해 Authentication 객체 리턴
    public Authentication getAuthentication(String token, Claims claims){
        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claim과 authorities 이용하여 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // 최종적으로 Authentication 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}