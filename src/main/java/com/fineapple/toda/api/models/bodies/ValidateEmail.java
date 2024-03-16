package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidEmail;
import lombok.*;

@Getter
@ToString
public final class ValidateEmail {
    @ValidEmail
    private String email;
    public ValidateEmail(){}
    @Builder
    public ValidateEmail(String email){
        this.email = email;
    }
}
