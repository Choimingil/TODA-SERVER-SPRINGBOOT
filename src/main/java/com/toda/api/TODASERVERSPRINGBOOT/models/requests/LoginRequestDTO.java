package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidEmail;
import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidPassword;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequestDTO {
    @ValidEmail
    public String id;
    @ValidPassword
    public String pw;

    public String toString(){
        return "id(email) : " +
                id +
                ", " +
                "password : " +
                pw;
    }
}
