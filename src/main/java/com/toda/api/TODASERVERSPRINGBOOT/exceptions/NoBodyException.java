package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageConversionException;

@Getter
public final class NoBodyException extends HttpMessageConversionException {
    public NoBodyException(String msg, of element) {
        super(msg);
        this.element = element;
    }

    @RequiredArgsConstructor
    @Getter
    public enum of{
        NO_BODY_EXCEPTION(102,"Body가 비었습니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
