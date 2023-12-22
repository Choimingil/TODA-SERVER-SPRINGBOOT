package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidPassword;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdatePw {
    @ValidPassword
    private String pw;
    public UpdatePw(){}
    @Builder
    public UpdatePw(String pw){
        this.pw = pw;
    }
}
