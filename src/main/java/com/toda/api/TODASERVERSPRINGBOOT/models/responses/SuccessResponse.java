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
        VALIDATE_EMAIL_SUCCESS(100,"사용 가능한 이메일입니다."),
        RIGHT_USER_EMAIL_SUCCESS(100, "성공"),
        NOT_USER_EMAIL_SUCCESS(200, "자신의 이메일이 아닙니다."),
        CURR_DEVICE_VERSION_SUCCESS(100,"최신 버전입니다."),
        PREV_DEVICE_VERSION_SUCCESS(200,"최신 버전이 아닙니다."),

        /**
         * UserController
         */
        CREATE_USER_SUCCESS(100, "회원가입이 완료되었습니다."),
        DELETE_USER_SUCCESS(100, "회원탈퇴가 완료되었습니다."),
        UPDATE_NAME_SUCCESS(100, "이름이 성공적으로 변경되었습니다."),
        UPDATE_PASSWORD_SUCCESS(100, "비밀번호가 성공적으로 변경되었습니다."),
        UPDATE_USER_SUCCESS(100, "유저 정보가 성공적으로 변경되었습니다."),

        /**
         * BASIC
         */
        SUCCESS(100, "성공"),
        GET_SUCCESS(100,"성공적으로 조회되었습니다.");



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