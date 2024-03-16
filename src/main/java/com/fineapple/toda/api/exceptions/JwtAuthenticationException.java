package com.fineapple.toda.api.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public final class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg) {
        super(msg);
        this.code = 401;
        this.message = "토큰 인증이 실패했습니다.";
    }

    private final int code;
    private final String message;
}
