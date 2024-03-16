package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidBinaryChoice;
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
