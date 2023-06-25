package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;

import java.util.*;


public abstract class Response extends AbstractModel implements BaseModel {
    final HashMap<String,?> response;

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
