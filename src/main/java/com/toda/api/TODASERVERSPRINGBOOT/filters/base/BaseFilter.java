package com.toda.api.TODASERVERSPRINGBOOT.filters.base;

import com.toda.api.TODASERVERSPRINGBOOT.handlers.FilterExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface BaseFilter {
    /**
     * 실제 필터 로직 수행하는 메소드
     * @param request
     * @param response
     */
    void doFilterLogic(HttpServletRequest request, HttpServletResponse response);

    /**
     * FilterExceptionHandler getter
     * @return
     */
    FilterExceptionHandler getFilterExceptionHandler();
}
