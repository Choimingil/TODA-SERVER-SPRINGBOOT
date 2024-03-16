package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidAppPw;
import lombok.*;

@Getter
@ToString
public final class AppPassword {
    @ValidAppPw
    private String appPW;
    public AppPassword(){}
    @Builder
    public AppPassword(String appPw){
        this.appPW = appPw;
    }
}
