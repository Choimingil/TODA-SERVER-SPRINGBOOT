package com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FilterExceptionHandler {
    // Singleton Pattern
    private FilterExceptionHandler(){}
    private static FilterExceptionHandler filterExceptionHandler = null;
    public static FilterExceptionHandler getInstance(){
        if(filterExceptionHandler == null){
            filterExceptionHandler = new FilterExceptionHandler();
        }
        return filterExceptionHandler;
    }
    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse.Builder(code,message).build();
        String json = new ObjectMapper().writeValueAsString(errorResponse.info);
        response.getWriter().write(json);
    }
}
