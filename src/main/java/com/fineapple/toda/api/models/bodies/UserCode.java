package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidUserCode;
import lombok.*;

@Getter
@ToString
public final class UserCode {
    @ValidUserCode
    private String userCode;

    public UserCode(){}

    @Builder
    public UserCode(String userCode){
        this.userCode = userCode;
    }
}
