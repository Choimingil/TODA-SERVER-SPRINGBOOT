package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidBinaryChoice;
import lombok.*;

@Getter
@ToString
public final class SaveFcmToken {
    private String token;
    @ValidBinaryChoice private String isAllowed;

    public SaveFcmToken(){}

    @Builder
    public SaveFcmToken(String token, String isAllowed){
        this.token = token;
        this.isAllowed = isAllowed;
    }
}
