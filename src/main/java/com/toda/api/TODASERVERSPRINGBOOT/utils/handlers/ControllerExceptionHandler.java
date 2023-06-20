package com.toda.api.TODASERVERSPRINGBOOT.utils.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public HashMap<String,Object> sendBadRequest(ValidationException e) {
        ErrorResponse response = new ErrorResponse.Builder(e.getCode(),e.getMessage()).build();
        return response.info;
    }
}