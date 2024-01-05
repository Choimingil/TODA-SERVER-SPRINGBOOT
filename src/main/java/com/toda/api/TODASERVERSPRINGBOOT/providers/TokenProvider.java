package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class TokenProvider extends AbstractProvider implements BaseProvider, InitializingBean {
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
     * 토큰을 유저 아이디로 변환
     * @param token
     * @return
     */
    public long getUserID(String token){
        Claims claims = getClaims(token);
        return Long.parseLong(String.valueOf(claims.get(TokenFields.USER_ID.value)));
    }

    /**
     * 토큰 디코딩
     * @param token
     * @return
     */
    public UserData decodeToken(String token){
        Claims claims = getClaims(token);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return UserData.builder()
                .userID(Long.parseLong(String.valueOf(claims.get(TokenFields.USER_ID.value))))
                .userCode(String.valueOf(claims.get(TokenFields.USER_CODE.value)))
                .appPassword(Integer.parseInt(String.valueOf(claims.get(TokenFields.APP_PASSWORD.value))))
                .email(String.valueOf(claims.get(TokenFields.EMAIL.value)))
                .userName(String.valueOf(claims.get(TokenFields.USER_NAME.value)))
                .createAt(LocalDateTime.parse(String.valueOf(claims.get(TokenFields.CREATE_AT.value)), formatter))
                .profile(String.valueOf(claims.get(TokenFields.PROFILE.value)))
                .build();
    }

    /**
     * 토큰 생성 메서드
     * @param authentication
     * @param userData
     * @return
     */
    public String createToken(Authentication authentication, UserData userData){
        String authorities = getAuthorities(authentication);
        Date validity = getValidity();
        return Jwts.builder()
                // subject : email
                .setSubject(authentication.getName())
                .claim(TokenFields.USER_ID.value, userData.getUserID())
                .claim(TokenFields.USER_CODE.value, userData.getUserCode())
                .claim(TokenFields.APP_PASSWORD.value, userData.getAppPassword())
                .claim(TokenFields.EMAIL.value, userData.getEmail())
                .claim(TokenFields.USER_NAME.value, userData.getUserName())
                .claim(TokenFields.CREATE_AT.value, userData.getCreateAt().toString())
                .claim(TokenFields.PROFILE.value, userData.getProfile())
                .claim(AUTHORITIES_KEY,authorities)
                .signWith(key, SignatureAlgorithm.HS512)
//                .setExpiration(validity)
                .compact();
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
     * Claims 생성
     * @param request
     * @return
     */
    public Claims getClaims(HttpServletRequest request){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(getToken(request))
                .getBody();
    }

    /**
     * 헤더값이 존재하는지 확인
     * @param request
     * @return
     */
    public boolean isExistHeader(HttpServletRequest request){
        String token = getToken(request);
        if(token == null) return false;
        return StringUtils.hasText(token);
    }

    /**
     * 헤더값의 형식이 올바른 경우 체크
     * @param request
     * @return
     */
    public boolean isValidHeader(HttpServletRequest request){
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        return jwtParser.isSigned(getToken(request));
    }

    /**
     * Authentication을 SecurityContextHolder에 저장
     * @param request
     */
    public void setSecurityContextHolder(HttpServletRequest request){
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(getToken(request)));
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



    /**
     * token 생성
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request){
        return request.getHeader(HEADER_NAME);
    }
}