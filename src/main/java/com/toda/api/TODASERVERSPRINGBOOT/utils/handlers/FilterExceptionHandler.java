package com.toda.api.TODASERVERSPRINGBOOT.utils.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.utils.providers.CurlProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public final class FilterExceptionHandler {
    private final CurlProvider curlProvider;

    public void setErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse.Builder(code,message).build();
        String json = new ObjectMapper().writeValueAsString(errorResponse.info);
        response.getWriter().write(json);
    }

    public void sendErrorToSlack(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception e
    ) throws IOException {
        curlProvider.sendSlackWithNoMdc(request,e);
        String message = "exception type :" + e.getClass() + " \nexception text : " + e.getMessage();

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse.Builder(999,message).build();
        String json = new ObjectMapper().writeValueAsString(errorResponse.info);
        response.getWriter().write(json);
    }
}
