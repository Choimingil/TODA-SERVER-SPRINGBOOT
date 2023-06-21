package com.toda.api.TODASERVERSPRINGBOOT.utils.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.CurlProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @Autowired
    public CurlProvider curlProvider;

    @ExceptionHandler(ValidationException.class)
    public HashMap<String,Object> sendBadRequest(ValidationException e) throws Exception {
        ErrorResponse response = new ErrorResponse.Builder(e.getCode(),e.getMessage()).build();
        return response.info;
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String,Object> sendErrorToSlack(HttpServletRequest request, Exception e) {
        curlProvider.sendSlack(request,e);
        ErrorResponse response = new ErrorResponse.Builder(999,e.getMessage()).build();
        return response.info;
    }
}