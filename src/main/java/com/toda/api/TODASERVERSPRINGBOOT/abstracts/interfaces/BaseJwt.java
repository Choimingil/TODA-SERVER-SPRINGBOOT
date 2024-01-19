package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.JwtHeader;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface BaseJwt {
    /**
     * 토큰을 유저 아이디로 변환
     * @param token
     * @return
     */
    long getUserID(String token);

    /**
     * Claims 생성
     * @param token
     * @return
     */
    Claims getClaims(String token);

//    /**
//     * Claims 객체에서 Subject 추출
//     * Subject : Email
//     * @param request
//     * @return
//     */
//    String getSubject(HttpServletRequest request);

    /**
     * token 생성
     * @param request
     * @return
     */
    String getToken(HttpServletRequest request);

    /**
     * 토큰 디코딩
     * @param token
     * @return
     */
    JwtHeader decodeToken(String token);

    /**
     * 토큰 생성 메서드
     * @param authentication
     * @param userDetail
     * @return
     */
    String createToken(Authentication authentication, UserDetail userDetail);

    /**
     * 헤더값이 존재하는지 확인
     * @param request
     * @return
     */
    boolean isExistHeader(HttpServletRequest request);

    /**
     * 헤더값의 형식이 올바른 경우 체크
     * @param request
     * @return
     */
    boolean isValidHeader(HttpServletRequest request);

    /**
     * Authentication을 SecurityContextHolder에 저장
     * @param request
     */
    void setSecurityContextHolder(HttpServletRequest request);
}
