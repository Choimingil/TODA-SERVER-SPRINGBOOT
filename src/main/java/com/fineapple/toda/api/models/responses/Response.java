package com.fineapple.toda.api.models.responses;

import java.util.*;


public abstract class Response {
    private final Map<String,?> response;

    abstract static class Builder<T extends Builder<T>>{
        Map<String,Object> response = new HashMap<>();

        public T add(String key, Object val){
            response.put(key,val);
            return self();
        }

        abstract Response build();

        protected abstract T self();
    }

    Response(Builder<?> builder){
        response = new HashMap<>(builder.response);
    }

    public Map<String, ?> getResponse() {
        return response;
    }
}
