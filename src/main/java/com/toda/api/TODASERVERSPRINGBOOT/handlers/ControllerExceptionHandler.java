package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
@RequiredArgsConstructor
public final class ControllerExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final SlackProvider slackProvider;

    @ExceptionHandler(ValidationException.class)
    public HashMap<String,?> setErrorResponse(ValidationException e) {
        return getErrorResponse(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public HashMap<String,?> checkBodyNull(RedisConnectionFailureException e) {
        return getErrorResponse(
                Exceptions.REDIS_CONNECTION_EXCEPTION.code(),
                Exceptions.REDIS_CONNECTION_EXCEPTION.message()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HashMap<String,?> checkBodyNull(HttpMessageNotReadableException e) {
        return getErrorResponse(
                Exceptions.NO_BODY_EXCEPTION.code(),
                Exceptions.NO_BODY_EXCEPTION.message()
        );
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String,?> sendErrorToSlack(Exception e) {
        slackProvider.sendSlackWithMdc(e);
        return getErrorResponse(999,getErrorMsg(e));
    }
}