package com.toda.api.TODASERVERSPRINGBOOT.utils.providers;

// 토큰의 생성, 토큰의 유효성 검증 등을 담당

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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
public class TokenProvider implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    public static final String HEADER_NAME = "x-access-token";
    public static Key key;
    private static String secret;
    private static long tokenValidityInMilliseconds;

    // @Value 값 static 변수에 할당
    @Value("${jwt.secret}")
    private void setSecret(String value) {
        secret = value;
    }
    @Value("${jwt.token-validity-in-seconds}")
    private void setTokenValidityInMilliseconds(long value) {
        tokenValidityInMilliseconds = value;
    }

    // Bean이 생성이 되고 주입을 받은 후에 secret값을 Base64로 Decode 해서 key 변수에 할당
    @Override
    public void afterPropertiesSet() {
        setKey();
    }

    private static void setKey(){
        logger.info("setKey pass");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        logger.info("set keyBytes pass");
        key = Keys.hmacShaKeyFor(keyBytes);
        logger.info("set Keys.hmacShaKeyFor(keyBytes) pass");
    }

    public static String createToken(
            Authentication authentication,
            UserInfoAllDAO userInfoAllDAO
    ){
        // authorities 설정
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 주의 : Test에서 새로운 만료 시간으로 추가해서 객체를 생성해도 원래 추가된 시간으로 반영됨
        // 이거 아마 싱글톤 때문에 객체 여러 개 생성하지 않고 하나 돌려써서 그런듯

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

    public static String resolveToken(HttpServletRequest request, String headerName){
        String token = request.getHeader(headerName);
        if(StringUtils.hasText(token)) return token;
        else throw new ValidationException(102,"헤더값이 인식되지 않습니다.");
    }

//     토큰의 유효성 검증 수행
    public static Claims validateToken(String token){
        // tokenProvider에 key가 할당되기 전에 인증 과정으로 넘어가는 경우 방지하기 위해 key가 초기화되지 않으면 초기화 함수 실행
        if(key == null) setKey();

        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        if(!jwtParser.isSigned(token)) throw new ValidationException(103, "잘못된 헤더값입니다.");
        else{
            Claims claims = jwtParser
                    .parseClaimsJws(token)
                    .getBody();

            if (    claims.get("userID") == null ||
                    claims.get("userCode") == null ||
                    claims.get("email") == null ||
                    claims.get("userName") == null ||
                    claims.get("appPassword") == null
            ) throw new ValidationException(103, "잘못된 헤더값입니다.");
            else return claims;
        }
    }

    // 토큰에 담겨있는 정보를 이용해 Authentication 객체 리턴
    public static Authentication getAuthentication(String token, Claims claims){
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