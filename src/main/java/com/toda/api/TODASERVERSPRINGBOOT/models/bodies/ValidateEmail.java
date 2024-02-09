package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidEmail;
import lombok.*;

@Getter
@ToString
public final class ValidateEmail {
    @ValidEmail private String email;
    public ValidateEmail(){}
    @Builder
    public ValidateEmail(String email){
        this.email = email;
    }
}
