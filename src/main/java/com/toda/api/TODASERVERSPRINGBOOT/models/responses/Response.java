package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import java.util.*;


public abstract class Response {
    final HashMap<String,Object> response;

    abstract static class Builder<T extends Builder<T>>{
        HashMap<String,Object> response = new HashMap<>();

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
}