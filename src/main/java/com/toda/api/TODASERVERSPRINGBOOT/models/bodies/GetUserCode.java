package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidUserCode;
import lombok.*;

@Getter
@ToString
public final class GetUserCode {
    @ValidUserCode private String userCode;

    public GetUserCode(){}

    @Builder
    public GetUserCode(String userCode){
        this.userCode = userCode;
    }
}
