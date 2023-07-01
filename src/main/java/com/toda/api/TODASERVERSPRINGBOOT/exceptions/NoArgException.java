package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class NoArgException extends NullPointerException{
    @RequiredArgsConstructor
    @Getter
    public enum of{
        NO_URI_EXCEPTION(101,"존재하지 않는 Uri입니다."),
        NO_HEADER_EXCEPTION(102,"헤더값이 인식되지 않습니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
