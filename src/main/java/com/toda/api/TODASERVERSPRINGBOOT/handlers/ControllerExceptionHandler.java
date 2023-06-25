package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.CurlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
@RequiredArgsConstructor
public final class ControllerExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final CurlProvider curlProvider;

    @ExceptionHandler(ValidationException.class)
    public HashMap<String,?> setErrorResponse(ValidationException e) {
        return getErrorResponse(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HashMap<String,?> checkBodyNull(HttpMessageNotReadableException e) {
        return getErrorResponse(102,"Body가 비었습니다.");
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String,?> sendErrorToSlack(Exception e) {
        curlProvider.sendSlackWithMdc(e);
        return getErrorResponse(999,getErrorMsg(e));
    }
}