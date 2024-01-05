package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

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
     * Exception 종류 별 실패를 MDC 값을 기준으로 리턴해주는 메소드
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    Map<String,?> getResponse(Exception e, int elementCode, String errorMessage);

    /**
     * Exception 종류 별 실패를 HttpServletRequest 값을 기준으로 리턴해주는 메소드
     * @param request
     * @param e
     * @param elementCode
     * @param errorMessage
     * @return
     */
    Map<String,?> getResponse(HttpServletRequest request, Exception e, int elementCode, String errorMessage);
}
