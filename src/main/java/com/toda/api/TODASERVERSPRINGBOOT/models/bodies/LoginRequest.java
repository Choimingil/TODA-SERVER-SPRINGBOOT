package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidEmail;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidPassword;
import lombok.*;

@Getter
@ToString
public final class LoginRequest {
    @ValidEmail private String id;
    @ValidPassword @NonNull private String pw;
    public LoginRequest(){}
    @Builder
    public LoginRequest(String id, @NonNull String pw){
        this.id = id;
        this.pw = pw;
    }
}
