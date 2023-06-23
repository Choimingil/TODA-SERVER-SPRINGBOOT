package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.sun.istack.NotNull;
import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidEmail;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public final class ValidateEmailDTO {
    @ValidEmail
    @NotNull
    public String email;

    public ValidateEmailDTO(){}

    public ValidateEmailDTO(String email){
        this.email = email;
    }

    public String toString(){
        return "email : " + email;
    }
}
