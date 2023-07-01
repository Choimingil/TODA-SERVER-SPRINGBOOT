package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import com.toda.api.TODASERVERSPRINGBOOT.utils.Success;

import java.util.HashMap;
import java.util.Map;

public class SuccessResponse extends Response {
    private SuccessResponse(Builder builder){
        super(builder);
        Map<String, ?> info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(int code, String message){
            this.response.put("isSuccess",true);
            this.response.put("code",code);
            this.response.put("message",message);
        }
        public Builder(Success success){
            this.response.put("isSuccess",true);
            this.response.put("code",success.code());
            this.response.put("message",success.message());
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