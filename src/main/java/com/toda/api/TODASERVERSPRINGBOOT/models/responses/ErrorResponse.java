package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import com.toda.api.TODASERVERSPRINGBOOT.utils.Exceptions;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse extends Response {
    private ErrorResponse(Builder builder){
        super(builder);
        Map<String, ?> info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(int code, String message){
            this.response.put("isSuccess",false);
            this.response.put("code",code);
            this.response.put("message",message);
        }

        public Builder(Exceptions exceptions){
            this.response.put("isSuccess",false);
            this.response.put("code",exceptions.code());
            this.response.put("message",exceptions.message());
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