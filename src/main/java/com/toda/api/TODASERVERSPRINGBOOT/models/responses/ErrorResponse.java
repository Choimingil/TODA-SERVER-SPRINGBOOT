package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import java.util.HashMap;

public class ErrorResponse extends Response {
    public HashMap<String,Object> info;

    private ErrorResponse(Builder builder){
        super(builder);
        info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(int code, String message){
            this.response.put("isSuccess",false);
            this.response.put("code",code);
            this.response.put("message",message);
        }

        @Override
        public ErrorResponse build() {
            return new ErrorResponse(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}