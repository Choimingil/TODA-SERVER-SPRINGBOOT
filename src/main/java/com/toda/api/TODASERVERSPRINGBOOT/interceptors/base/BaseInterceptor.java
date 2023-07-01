package com.toda.api.TODASERVERSPRINGBOOT.interceptors.base;

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
}
