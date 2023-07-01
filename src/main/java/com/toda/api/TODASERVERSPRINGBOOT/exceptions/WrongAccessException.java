package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;


@AllArgsConstructor
@Getter
public final class WrongAccessException extends IllegalStateException {
    @RequiredArgsConstructor
    @Getter
    public enum of{
        REDIS_CONNECTION_EXCEPTION(500,"Redis에 정상적으로 등록되지 않았습니다."),
        MDC_SETTING_EXCEPTION(500,"MDC가 정상적으로 설정되지 않았습니다."),
        SET_BODY_TO_MDC_EXCEPTION(500,"MDC에 Request_body를 넣는 중 오류가 발생했습니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
