package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.sun.istack.NotNull;
import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidAppPw;
import lombok.*;

@Getter
@Setter
public class CheckTokenDTO {
    @ValidAppPw
    @NotNull
    public String appPW;

    public CheckTokenDTO(){}

    public CheckTokenDTO(String appPw){
        this.appPW = appPw;
    }

    public String toString(){
        return "appPassword : " + appPW;
    }
}
