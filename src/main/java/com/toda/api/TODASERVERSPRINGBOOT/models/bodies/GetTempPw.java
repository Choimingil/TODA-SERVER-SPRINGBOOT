package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidEmail;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class GetTempPw {
    @ValidEmail
    private String id;
    public GetTempPw(){}
    @Builder
    public GetTempPw(String id){
        this.id = id;
    }
}
