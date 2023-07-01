package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

public class SuccessResponse extends Response {
    @RequiredArgsConstructor
    @Getter
    public enum of{
        /**
         * AuthController
         */
        LOGIN_SUCCESS(100,"성공적으로 로그인되었습니다."),
        DECODE_TOKEN_SUCCESS(100,"자체 로그인 성공"),
        CHECK_TOKEN_SUCCESS(100,"유효한 유저입니다."),

        /**
         * SystemController
         */
        VALIDATE_EMAIL_SUCCESS(100,"사용 가능한 이메일입니다.");

        private final int code;
        private final String message;
    }
    private SuccessResponse(Builder builder){
        super(builder);
        Map<String, ?> info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(of element){
            this.response.put("isSuccess",true);
            this.response.put("code",element.getCode());
            this.response.put("message",element.getMessage());
        }

        @Override
        public SuccessResponse build() {
            return new SuccessResponse(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}