package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidAppPw;
import lombok.*;

@Getter
@ToString
public final class AppPassword {
    @ValidAppPw private String appPW;
    public AppPassword(){}
    @Builder
    public AppPassword(String appPw){
        this.appPW = appPw;
    }
}
