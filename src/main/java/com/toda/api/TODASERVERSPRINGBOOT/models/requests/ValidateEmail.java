package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidEmail;
import lombok.*;

@Getter
public final class ValidateEmail {
    @ValidEmail private String email;
    public ValidateEmail(){}
    @Builder
    public ValidateEmail(String email){
        this.email = email;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("email : ");
        sb.append(email);
        return sb.toString();
    }
}
