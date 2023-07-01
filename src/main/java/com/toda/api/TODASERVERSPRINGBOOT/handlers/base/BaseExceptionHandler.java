package com.toda.api.TODASERVERSPRINGBOOT.handlers.base;

import java.util.HashMap;
import java.util.Map;

public interface BaseExceptionHandler {
    /**
     * ErrorResponse 생성 메소드
     * @param code
     * @param message
     * @return
     */
//    Map<String,?> getErrorResponse(int code, String message);

    /**
     * 예상하지 못한 에러 발생 시 Error Message 생성 메소드
     * @param e
     * @return
     */
    String getErrorMsg(Exception e);
}
