package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.*;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public final class FilterExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final ObjectMapper objectMapper;
    private final SlackProvider slackProvider;

    public void getResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse;

        try {
            /**
             * JwtAccessDeniedException Handler
             */
            if(e.getClass() == JwtAccessDeniedException.class){
                JwtAccessDeniedException exception = (JwtAccessDeniedException) e;
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,exception,exception.getCode(),exception.getMessage())
                );
            }

            /**
             * JwtAuthenticationException Handler
             */
            else if(e.getClass() == JwtAuthenticationException.class){
                JwtAuthenticationException exception = (JwtAuthenticationException) e;
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,exception,exception.getCode(),exception.getMessage())
                );
            }

            /**
             * NoArgException Handler
             */
            else if(e.getClass() == NoArgException.class){
                NoArgException exception = (NoArgException) e;
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * WrongArgException Handler
             */
            else if(e.getClass() == WrongArgException.class){
                WrongArgException exception = (WrongArgException) e;
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * NoBodyException Handler
             */
            else if(e.getClass() == NoBodyException.class){
                NoBodyException exception = (NoBodyException) e;
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * Rest Exceptions Handler
             */
            else{
                jsonResponse = objectMapper.writeValueAsString(
                        getResponse(request,e,FailResponse.of.UNKNOWN_EXCEPTION.getCode(), e.getMessage())
                );
            }

            response.getWriter().write(jsonResponse);
        }
        catch (IOException exception){
            logger.error("IOException");
        }

    }
}
