package com.fineapple.toda.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public final class WrongAccessException extends IllegalStateException {
    @RequiredArgsConstructor
    @Getter
    public enum of{
        HTTP_CONNECTION_EXCEPTION(500,"요청이 정상적으로 실행되지 않았습니다."),
        HTTP_CLIENT_CREATE_EXCEPTION(500,"HttpClient가 정상적으로 생성되지 않았습니다."),
        SEND_FCM_EXCEPTION(500,"FCM 메시지가 정상적으로 발송되지 않았습니다."),
        FCM_BODY_EXCEPTION(500,"잘못된 FCM 바디 형식입니다."),
        SEND_MAIL_EXCEPTION(500,"메일이 정상적으로 발송되지 않았습니다."),
        SEND_SLACK_EXCEPTION(500,"SLACK 메시지가 정상적으로 발송되지 않았습니다."),
        REDIS_CONNECTION_EXCEPTION(500,"Redis에 정상적으로 등록되지 않았습니다."),
        MQ_CONNECTION_EXCEPTION(500,"메시지 큐에 정상적으로 접속되지 않았습니다."),
        MDC_SETTING_EXCEPTION(500,"MDC가 정상적으로 설정되지 않았습니다."),
        SET_BODY_TO_MDC_EXCEPTION(500,"MDC에 Request_body를 넣는 중 오류가 발생했습니다."),
        READ_TXT_EXCEPTION(500,"텍스트 파일을 읽는 중 오류가 발생했습니다."),
        SQL_EXCEPTION(500,"DB 연결 중 오류가 발생했습니다.");

        private final int code;
        private final String message;
    }

    private final of element;
}
