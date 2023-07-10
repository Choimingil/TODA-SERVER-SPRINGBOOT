package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.*;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.FailResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public final class ControllerExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final SlackProvider slackProvider;

    /**
     * NoArgException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(NoArgException.class)
    public Map<String,?> getResponseOfNoArgException(NoArgException e) {
        return getResponse(e, e.getElement().getCode(), e.getElement().getMessage());
    }

    /**
     * WrongArgException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(WrongArgException.class)
    public Map<String,?> getResponseOfWrongArgException(WrongArgException e) {
        return getResponse(e, e.getElement().getCode(), e.getElement().getMessage());
    }

    /**
     * RedisConnectionFailureException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public Map<String,?> getResponseOfRedisConnectionFailureException(RedisConnectionFailureException e) {
        return getResponse(e, WrongAccessException.of.REDIS_CONNECTION_EXCEPTION.getCode(), WrongAccessException.of.REDIS_CONNECTION_EXCEPTION.getMessage());
    }

    /**
     * WrongAccessException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(WrongAccessException.class)
    public Map<String,?> getResponseOfWrongAccessException(WrongAccessException e) {
        return getResponse(e, e.getElement().getCode(), e.getElement().getMessage());
    }

    /**
     * HttpMessageConversionException Handler
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public Map<String,?> getResponseOfHttpMessageConversionException(HttpMessageConversionException e) {
        return getResponse(e, NoBodyException.of.NO_BODY_EXCEPTION.getCode(), NoBodyException.of.NO_BODY_EXCEPTION.getMessage());
    }

    /**
     * Rest Exceptions Handler
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Map<String,?> getResponseOfRestException(Exception e) {
        return getResponse(e, FailResponse.of.UNKNOWN_EXCEPTION.getCode(), getErrorMsg(e));
    }

    @Override
    public SlackProvider getSlackProvider() {
        return slackProvider;
    }
}