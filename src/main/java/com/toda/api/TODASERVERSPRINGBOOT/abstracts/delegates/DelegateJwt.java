package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseJwt;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class DelegateJwt implements BaseJwt, InitializingBean {
    /* public variables */
    public static final String HEADER_NAME = "x-access-token";
    public static String SKIP_VALUE;

    /* private variables */
    private static final String AUTHORITIES_KEY = "auth";
    private Key key;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInMilliseconds;

    @Override
    public long getUserID(String token) {
        Claims claims = getClaims(token);
        if(claims == null) throw new NoArgException(NoArgException.of.NULL_PARAM_EXCEPTION);
        return Long.parseLong(String.valueOf(claims.get(TokenFields.USER_ID.value)));
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    @Override
    public String getSubject(HttpServletRequest request) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(getToken(request)).getBody().getSubject();
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_NAME);
    }

    @Override
    public UserData decodeToken(String token) {
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

    @Override
    public String createToken(Authentication authentication, UserData userData) {
        String authorities = getAuthorities(authentication);
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
//                .setExpiration(getValidity())
                .compact();
    }

    @Override
    public boolean isExistHeader(HttpServletRequest request) {
        String token = getToken(request);
        if(token == null) return false;
        return StringUtils.hasText(token);
    }

    @Override
    public boolean isValidHeader(HttpServletRequest request) {
        return Jwts.parserBuilder().setSigningKey(key).build().isSigned(getToken(request));
    }

    @Override
    public void setSecurityContextHolder(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(getToken(request)));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
        SKIP_VALUE = secret;
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
                        .distinct()
                        .collect(Collectors.toList());

        // claim과 authorities 이용하여 User 객체 생성
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        // 최종적으로 Authentication 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
