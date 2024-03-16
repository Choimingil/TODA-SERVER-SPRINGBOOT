package com.fineapple.toda.api.abstracts.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface BaseFilter {
    /**
     * 실제 필터 로직 수행하는 메소드
     * @param request
     * @param response
     */
    void doFilterLogic(HttpServletRequest request, HttpServletResponse response);
}
