package com.fineapple.toda.api.abstracts.interfaces;

import com.fineapple.toda.api.entities.mappings.UserDetail;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface BaseJwt {
    /**
     * Claims 생성
     * @param token
     * @return
     */
    Claims getClaims(String token);

    /**
     * token 생성
     * @param request
     * @return
     */
    String getToken(HttpServletRequest request);

    /**
     * 토큰 디코딩 후 이메일 정보 가져오기
     * @param token
     * @return
     */
    String getEmailWithDecodeToken(String token);

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
