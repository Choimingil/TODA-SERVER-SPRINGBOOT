package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidAppPw;
import lombok.*;

@Getter
public final class CheckToken extends AbstractModel implements BaseModel {
    @ValidAppPw private String appPW;
    public CheckToken(){}
    @Builder
    public CheckToken(String appPw){
        this.appPW = appPw;
    }

    @Override
    public String toString(){
        return "appPassword : " + appPW;
    }
}
