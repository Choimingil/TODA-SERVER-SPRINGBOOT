package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidUserCode;
import lombok.*;

@Getter
@ToString
public final class UserCode {
    @ValidUserCode private String userCode;

    public UserCode(){}

    @Builder
    public UserCode(String userCode){
        this.userCode = userCode;
    }
}
