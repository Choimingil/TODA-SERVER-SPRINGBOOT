package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import java.util.HashMap;

public class SuccessResponse extends Response {
    public HashMap<String,?> info;

    private SuccessResponse(Builder builder){
        super(builder);
        info = builder.response;
    }

    public static class Builder extends Response.Builder<Builder>{
        public Builder(int code, String message){
            this.response.put("isSuccess",true);
            this.response.put("code",code);
            this.response.put("message",message);
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