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
        WRONG_DEVICE_TYPE_EXCEPTION(103,"잘못된 type 값입니다."),
        WRONG_HEADER_EXCEPTION(103, "잘못된 헤더값입니다."),
        WRONG_TOKEN_DATA_EXCEPTION(103,"토큰과 유저 정보가 일치하지 않습니다."),
        WRONG_APP_PASSWORD_EXCEPTION(103,"앱 비밀번호가 잘못됐습니다."),
        WRONG_BODY_EXCEPTION(104,"잘못된 Body입니다."),
        NOT_TODA_USER_EXCEPTION(103,"TODA 계정이 아닙니다.(카카오 로그인 등은 사용 불가)"),
        SAME_PASSWORD_EXCEPTION(104,"이전의 비밀번호와 똑같습니다."),
        WRONG_REMIND_FCM_EXCEPTION(102,"리마인드 알림이 거절된 상태 혹은 토큰이 존재하지 않은 상태입니다."),
        WRONG_DIARY_STATUS_EXCEPTION(103,"존재하지 않는 코드입니다."),
        WRONG_DIARY_COLOR_EXCEPTION(104,"존재하지 않는 색입니다."),
        WRONG_TYPE_EXCEPTION(500,"입력 타입과 다른 타입입니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
