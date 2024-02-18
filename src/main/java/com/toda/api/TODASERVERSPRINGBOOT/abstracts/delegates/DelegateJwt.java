package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractUtil;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseJwt;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public final class DelegateJwt extends AbstractUtil implements BaseJwt, InitializingBean {
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

    public DelegateJwt(DelegateDateTime delegateDateTime, DelegateFile delegateFile, DelegateStatus delegateStatus) {
        super(delegateDateTime, delegateFile, delegateStatus);
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_NAME);
    }

    @Override
    public String getEmailWithDecodeToken(String token) {
        Claims claims = getClaims(token);

//        String.valueOf(Object obj) 메서드는 null을 인자로 받았을 때, "null"이라는 문자열을 반환합니다.
//        따라서, claims.get("id")가 null을 반환하더라도 ver1은 실제로 null이 아닌 "null" 문자열이 되어버립니다.
//        이것이 if (ver1 != null) 조건을 true로 만들고, 조건문이 통과되는 이유입니다.
//        즉, ver1 변수는 절대 null이 될 수 없으며, 최소한 "null" 문자열이 됩니다. 이 때문에 if 문은 항상 참이 되어 조건문 내부의 코드가 실행됩니다.

        Object ver1 = claims.get("id");
        if(ver1 != null) return String.valueOf(ver1);

        Object ver2 = claims.get(TokenFields.EMAIL.value);
        if(ver2 != null) return String.valueOf(ver2);

        throw new WrongArgException(WrongArgException.of.WRONG_HEADER_EXCEPTION);
    }

    @Override
    public String createToken(Authentication authentication, UserDetail userDetail) {
        return Jwts.builder()
                .claim(TokenFields.CREATE_AT.value, toStringDateFullTime(LocalDateTime.now()))
                .claim(TokenFields.USER_ID.value, userDetail.getUser().getUserID())
                .claim(TokenFields.EMAIL.value, userDetail.getUser().getEmail())
                .claim(TokenFields.APP_PASSWORD.value, userDetail.getUser().getAppPassword())
                .setHeaderParam("typ", "JWT")
                .signWith(key, SignatureAlgorithm.HS256)
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
//        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
//        key = Keys.hmacShaKeyFor(keyBytes);
//        SKIP_VALUE = secret;

        byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        SKIP_VALUE = secret;
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
        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream("ROLE_USER".split(","))
                        .map(SimpleGrantedAuthority::new)
                        .distinct()
                        .collect(Collectors.toList());

        // claim과 authorities 이용하여 User 객체 생성
        String email = getEmailWithDecodeToken(token);
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(email, "", authorities);

        // 최종적으로 Authentication 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
