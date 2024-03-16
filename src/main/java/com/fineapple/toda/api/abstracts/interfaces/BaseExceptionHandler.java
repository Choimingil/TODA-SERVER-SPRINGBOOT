package com.fineapple.toda.api.abstracts.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface BaseExceptionHandler {
    /**
     * 예상하지 못한 에러 발생 시 Error Message 생성 메소드
     * @param e
     * @return
     */
    String getErrorMsg(Exception e);

    /**
     * Spring Container 내에서 발생한 에러 캐치
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    Map<String,?> getErrorSpringContainer(Exception e, int elementCode, String errorMessage);

    /**
     * Filter 레벨에서 발생한 에러 캐치
     * @param request
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    Map<String,?> getErrorFilter(HttpServletRequest request, Exception e, int elementCode, String errorMessage);
}
