package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidAppPw;
import lombok.*;

@Getter
@ToString
public final class GetAppPassword {
    @ValidAppPw private String appPW;
    public GetAppPassword(){}
    @Builder
    public GetAppPassword(String appPw){
        this.appPW = appPw;
    }
}
