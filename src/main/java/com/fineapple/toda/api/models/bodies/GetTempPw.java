package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidEmail;
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
