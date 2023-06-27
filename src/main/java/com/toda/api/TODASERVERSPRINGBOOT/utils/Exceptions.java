package com.toda.api.TODASERVERSPRINGBOOT.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Exceptions {

    /**
     * Header
     */
    NO_URI_EXCEPTION(101,"존재하지 않는 Uri입니다."),
    NO_HEADER_EXCEPTION(102,"헤더값이 인식되지 않습니다."),
    WRONG_HEADER_EXCEPTION(103, "잘못된 헤더값입니다."),
    WRONG_TOKEN_DATA_EXCEPTION(103,"토큰과 유저 정보가 일치하지 않습니다."),

    /**
     * Body
     */
    NO_BODY_EXCEPTION(102,"Body가 비었습니다."),
    WRONG_APP_PASSWORD_EXCEPTION(103,"앱 비밀번호가 잘못됐습니다."),
    NOT_VALID_EMAIL_EXCEPTION(103, "유효한 이메일이 아닙니다."),
    EXIST_EMAIL_EXCEPTION(104,"이미 존재하는 이메일입니다."),
    WRONG_BODY_EXCEPTION(104,"잘못된 Body입니다."),

    /**
     * Logic
     */

    WRONG_TYPE_EXCEPTION(500,"입력 타입과 다른 타입입니다."),

    /**
     * Utils
     */
    REDIS_CONNECTION_EXCEPTION(500,"Redis에 정상적으로 등록되지 않았습니다."),
    MDC_SETTING_EXCEPTION(500,"MDC가 정상적으로 설정되지 않았습니다."),
    SET_BODY_TO_MDC_EXCEPTION(500,"MDC에 Request_body를 넣는 중 오류가 발생했습니다.")
    ;

    private final int code;
    private final String message;

    public int code(){ return code; }
    public String message() {return message; }
}
