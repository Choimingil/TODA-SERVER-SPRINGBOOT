package com.toda.api.TODASERVERSPRINGBOOT.interceptors.base;

import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface BaseInterceptor {
    /**
     * 컨트롤러 메서드가 실행되기 전 로직 수행하는 메소드
     * @param request
     * @param response
     * @param handler
     */
    boolean doPreHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler);
    /**
     * 컨트롤러 메서드 실행 후 로직 수행하는 메소드
     * @param request
     * @param response
     * @param handler
     */
    void doPostHandleLogic(HttpServletRequest request, HttpServletResponse response, Object handler);

    /**
     * 유효한 MDC 로그 키인지 검사
     * @return
     */
    boolean isMdcSet();

    /**
     * MDC에 값 추가
     * @param request
     */
    void setMdc(HttpServletRequest request);
}
