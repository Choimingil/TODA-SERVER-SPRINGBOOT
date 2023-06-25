package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidEmail;
import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidPassword;
import lombok.*;

@Getter
public final class LoginRequest {
    @ValidEmail private String id;
    @ValidPassword @NonNull private String pw;
    public LoginRequest(){}
    @Builder
    public LoginRequest(String id, @NonNull String pw){
        this.id = id;
        this.pw = pw;
    }

    @Override
    public String toString(){
        return "id(email) : " +
                id +
                ", " +
                "password : " +
                pw;
    }
}
