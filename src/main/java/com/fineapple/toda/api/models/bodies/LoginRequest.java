package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidPassword;
import com.fineapple.toda.api.validators.annotations.ValidEmail;
import lombok.*;

@Getter
@ToString
public final class LoginRequest {
    @ValidEmail private String id;
    @ValidPassword
    @NonNull private String pw;
    public LoginRequest(){}
    @Builder
    public LoginRequest(String id, @NonNull String pw){
        this.id = id;
        this.pw = pw;
    }
}
