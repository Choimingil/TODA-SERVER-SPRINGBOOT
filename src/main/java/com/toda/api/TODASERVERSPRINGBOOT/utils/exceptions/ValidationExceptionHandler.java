package com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public HashMap<String,Object> sendBadRequest(ValidationException e) {
        ErrorResponse response = new ErrorResponse.Builder(e.getCode(),e.getMessage()).build();
        return response.info;
    }
}