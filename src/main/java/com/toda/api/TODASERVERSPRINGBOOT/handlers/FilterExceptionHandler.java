package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.DelegateJms;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class FilterExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    public FilterExceptionHandler(DelegateJms delegateJms) {
        super(delegateJms);
    }

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
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getCode(),exception.getMessage())
                );
            }

            /**
             * JwtAuthenticationException Handler
             */
            else if(e.getClass() == JwtAuthenticationException.class){
                JwtAuthenticationException exception = (JwtAuthenticationException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getCode(),exception.getMessage())
                );
            }

            /**
             * NoArgException Handler
             */
            else if(e.getClass() == NoArgException.class){
                NoArgException exception = (NoArgException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * WrongArgException Handler
             */
            else if(e.getClass() == WrongArgException.class){
                WrongArgException exception = (WrongArgException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * WrongAccessException Handler
             */
            else if(e.getClass() == WrongAccessException.class){
                WrongAccessException exception = (WrongAccessException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * BusinessLogicException Handler
             */
            else if(e.getClass() == BusinessLogicException.class){
                BusinessLogicException exception = (BusinessLogicException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * NoBodyException Handler
             */
            else if(e.getClass() == NoBodyException.class){
                NoBodyException exception = (NoBodyException) e;
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,exception,exception.getElement().getCode(),exception.getElement().getMessage())
                );
            }

            /**
             * Rest Exceptions Handler
             */
            else{
                jsonResponse = new ObjectMapper().writeValueAsString(
                        getErrorFilter(request,e,FailResponse.of.UNKNOWN_EXCEPTION.getCode(), e.getMessage())
                );
            }

            response.getWriter().write(jsonResponse);
        }
        catch (IOException exception){
            logger.error("IOException");
        }

    }

    private String parsingException(Exception e){
        String message = e.getMessage();
        String[] arr = message.split(": ");
        String[] parse = arr[1].split("\\.");
        int length = parse.length;
        return parse[length-1];
    }
}
