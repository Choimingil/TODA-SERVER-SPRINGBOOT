package com.toda.api.TODASERVERSPRINGBOOT.handlers;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.ValidationException;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.AbstractExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.handlers.base.BaseExceptionHandler;
import com.toda.api.TODASERVERSPRINGBOOT.models.responses.ErrorResponse;
import com.toda.api.TODASERVERSPRINGBOOT.providers.SlackProvider;
import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public final class ControllerExceptionHandler extends AbstractExceptionHandler implements BaseExceptionHandler {
    private final SlackProvider slackProvider;

    @ExceptionHandler(ValidationException.class)
    public Map<String,?> setErrorResponse(ValidationException e) {
        return new ErrorResponse.Builder(e.getExceptions()).build().getResponse();
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Map<String,?> checkBodyNull(RedisConnectionFailureException e) {
        return new ErrorResponse.Builder(Exceptions.REDIS_CONNECTION_EXCEPTION).build().getResponse();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String,?> checkBodyNull(HttpMessageNotReadableException e) {
        return new ErrorResponse.Builder(Exceptions.NO_BODY_EXCEPTION).build().getResponse();
    }

    @ExceptionHandler(Exception.class)
    public Map<String,?> sendErrorToSlack(Exception e) {
        slackProvider.doSlack(e);
        return new ErrorResponse.Builder(Exceptions.UNKNOWN_EXCEPTION).build().getResponse();
    }
}