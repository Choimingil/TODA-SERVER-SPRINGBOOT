package com.toda.api.TODASERVERSPRINGBOOT.exceptions;

import lombok.Getter;

import java.nio.file.AccessDeniedException;

@Getter
public final class JwtAccessDeniedException extends AccessDeniedException {
    public JwtAccessDeniedException(String file) {
        super(file);
        this.code = 403;
        this.message = "현재 API의 사용 권한이 존재하지 않습니다.";
    }

    private final int code;
    private final String message;
}
