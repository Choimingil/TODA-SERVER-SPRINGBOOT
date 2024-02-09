package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

public class FailResponse extends Response {
    @RequiredArgsConstructor
    @Getter
    public enum of{
        WRONG_EMAIL_EXCEPTION(103, "유효한 이메일이 아닙니다."),
        EXIST_EMAIL_EXCEPTION(104,"이미 존재하는 이메일입니다."),

        UNKNOWN_EXCEPTION(999,"알 수 없는 에러가 발생했습니다.");

        private final int code;
        private final String message;
    }
    private FailResponse(Builder builder){
        super(builder);
        Map<String, ?> info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(int code, String message){
            this.response.put("isSuccess",false);
            this.response.put("code",code);
            this.response.put("message",message);
        }

        public Builder(of element){
            this.response.put("isSuccess",false);
            this.response.put("code",element.getCode());
            this.response.put("message",element.getMessage());
        }

        @Override
        public FailResponse build() {
            return new FailResponse(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}