package com.toda.api.TODASERVERSPRINGBOOT.utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Success {
    /**
     * AuthController
     */
    LOGIN_SUCCESS(100,"성공적으로 로그인되었습니다."),
    DECODE_TOKEN_SUCCESS(100,"자체 로그인 성공"),
    CHECK_TOKEN_SUCCESS(100,"유효한 유저입니다."),

    /**
     * SystemController
     */
    VALIDATE_EMAIL_SUCCESS(100,"사용 가능한 이메일입니다.")
    ;

    private final int code;
    private final String message;

    public int code(){ return code; }
    public String message() {return message; }
}
