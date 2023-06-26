package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class TokenProvider extends AbstractProvider implements BaseProvider {
    public static final String HEADER_NAME = "x-access-token";
    private Key key;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInMilliseconds;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * token 생성
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request){
        return request.getHeader(HEADER_NAME);
    }

    /**
     * Claims 생성
     * @param token
     * @return
     */
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 헤더값이 존재하는지 확인
     * @param token
     * @return
     */
    public boolean isExistHeader(String token){
        return StringUtils.hasText(token);
    }

    /**
     * 헤더값의 형식이 올바른 경우 체크
     * @param token
     * @return
     */
    public boolean isValidHeader(String token){
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        return jwtParser.isSigned(token);
    }



    /**
     * 토큰 생성
     * @param authentication
     * @param userInfoAllDao
     * @return
     */
    public String createToken(Authentication authentication, UserInfoAllDao userInfoAllDao){
        String authorities = getAuthorities(authentication);
        Date validity = getValidity();
        return Jwts.builder()
                // subject : email
                .setSubject(authentication.getName())
                .claim("userID",userInfoAllDao.getUserID())
                .claim("userCode",userInfoAllDao.getUserCode())
                .claim("email",userInfoAllDao.getEmail())
                .claim("userName",userInfoAllDao.getUserName())
                .claim("appPassword",userInfoAllDao.getAppPassword())
                .claim(AuthenticationProvider.AUTHORITIES_KEY,authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * authorities 설정
     * @param authentication
     * @return
     */
    private String getAuthorities(Authentication authentication){
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * 토큰 만료 시간 설정
     * @return
     */
    private Date getValidity(){
        long now = (new Date()).getTime();
        return new Date(now + tokenValidityInMilliseconds);
    }
}