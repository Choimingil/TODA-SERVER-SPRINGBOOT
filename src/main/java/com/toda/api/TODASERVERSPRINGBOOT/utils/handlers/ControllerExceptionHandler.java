package com.toda.api.TODASERVERSPRINGBOOT.utils.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.CurlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @Autowired
    public CurlProvider curlProvider;

    @ExceptionHandler(ValidationException.class)
    public HashMap<String,Object> sendBadRequest(ValidationException e) {
        ErrorResponse response = new ErrorResponse.Builder(e.getCode(),e.getMessage()).build();
        return response.info;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HashMap<String,Object> checkBodyNull(HttpMessageNotReadableException e) {
        ErrorResponse response = new ErrorResponse.Builder(102,"Body가 비었습니다.").build();
        return response.info;
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String,Object> sendErrorToSlack(Exception e) {
        curlProvider.sendSlack(e);
        String message = "exception type :" + e.getClass() + " \nexception text : " + e.getMessage();
        ErrorResponse response = new ErrorResponse.Builder(999, message).build();
        return response.info;
    }
}