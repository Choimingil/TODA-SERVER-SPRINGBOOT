package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public final class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg) {
        super(msg);
        this.code = 403;
        this.message = "현재 API의 사용 권한이 존재하지 않습니다.";
    }

    private final int code;
    private final String message;
}
