package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class WrongArgException extends IllegalArgumentException{
    @RequiredArgsConstructor
    @Getter
    public enum of{
        WRONG_HEADER_EXCEPTION(103, "잘못된 헤더값입니다."),
        WRONG_TOKEN_DATA_EXCEPTION(103,"토큰과 유저 정보가 일치하지 않습니다."),
        WRONG_APP_PASSWORD_EXCEPTION(103,"앱 비밀번호가 잘못됐습니다."),
        WRONG_BODY_EXCEPTION(104,"잘못된 Body입니다."),
        WRONG_TYPE_EXCEPTION(500,"입력 타입과 다른 타입입니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
