package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class TokenProvider extends AbstractProvider implements BaseProvider {
    public static final String HEADER_NAME = "x-access-token";
    private static final String AUTHORITIES_KEY = "auth";
    public static String SKIP_VALUE;
    private Key key;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInMilliseconds;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        SKIP_VALUE = secret;
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
     * 토큰 생성 메서드
     * @param authentication
     * @param user
     * @return
     */
    public String createToken(Authentication authentication, User user){
        String authorities = getAuthorities(authentication);
        Date validity = getValidity();
        return Jwts.builder()
                // subject : email
                .setSubject(authentication.getName())
                .claim("userID",user.getUserID())
                .claim("userCode",user.getUserCode())
                .claim("email",user.getEmail())
                .claim("userName",user.getUserName())
                .claim("appPassword",user.getAppPassword())
                .claim(AUTHORITIES_KEY,authorities)
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
        long now = Instant.now().toEpochMilli();
        return new Date(now + tokenValidityInMilliseconds);
    }

    /**
     * Authentication을 SecurityContextHolder에 저장
     * @param token
     */
    public void setSecurityContextHolder(String token){
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
    }

    /**
     * 토큰에 담겨있는 정보를 이용해 Authentication 객체 리턴
     * @param token
     * @return
     */
    private Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claim과 authorities 이용하여 User 객체 생성
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        // 최종적으로 Authentication 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public long getUserID(String token){
        Claims claims = getClaims(token);
        return Long.parseLong(String.valueOf(claims.get("userID")));
    }
}