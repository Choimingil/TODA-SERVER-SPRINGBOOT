package com.toda.api.TODASERVERSPRINGBOOT.handlers.base;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;

import java.util.HashMap;

public abstract class AbstractExceptionHandler implements BaseExceptionHandler {
    /**
     * ErrorResponse 생성 메소드 구현
     * @param code
     * @param message
     * @return
     */
    @Override
    public HashMap<String,Object> getErrorResponse(int code, String message){
        ErrorResponse response = new ErrorResponse.Builder(code, message).build();
        return response.info;
    }

    /**
     * 예상하지 못한 에러 발생 시 Error Message 생성 메소드 구현
     * @param e
     * @return
     */
    @Override
    public String getErrorMsg(Exception e){
        return "exception type :" + e.getClass() + " \nexception text : " + e.getMessage();
    }
}
