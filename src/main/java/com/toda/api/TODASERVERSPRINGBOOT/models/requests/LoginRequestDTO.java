package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.sun.istack.NotNull;
import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidEmail;
import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidPassword;
import lombok.*;

@Getter
@RequiredArgsConstructor
public final class LoginRequestDTO {
    @ValidEmail
    @NotNull
    public String id;

    @ValidPassword
    @NotNull
    public String pw;

    public String toString(){
        return "id(email) : " +
                id +
                ", " +
                "password : " +
                pw;
    }
}
