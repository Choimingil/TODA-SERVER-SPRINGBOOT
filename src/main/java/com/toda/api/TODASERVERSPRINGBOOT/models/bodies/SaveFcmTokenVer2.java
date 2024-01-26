package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidBinaryChoice;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class SaveFcmTokenVer2 {
    private String token;
    @ValidBinaryChoice
    private String isAllowed;
    private int type;

    public SaveFcmTokenVer2(){}

    @Builder
    public SaveFcmTokenVer2(String token, String isAllowed, int type){
        this.token = token;
        this.isAllowed = isAllowed;
        this.type = type;
    }
}
