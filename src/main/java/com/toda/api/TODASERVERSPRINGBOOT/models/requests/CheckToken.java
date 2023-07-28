package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidAppPw;
import lombok.*;

@Getter
@ToString
public final class CheckToken {
    @ValidAppPw private String appPW;
    public CheckToken(){}
    @Builder
    public CheckToken(String appPw){
        this.appPW = appPw;
    }
}
