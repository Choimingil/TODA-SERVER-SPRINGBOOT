package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import lombok.*;

@Getter
public final class DecodeTokenResponseDto extends AbstractModel implements BaseModel {
    private long id;
    @NonNull private String pw;
    @NonNull private int appPw;

    public DecodeTokenResponseDto(){}

    @Builder
    public DecodeTokenResponseDto(
            long id,
            @NonNull String pw,
            @NonNull int appPw
    ){
        this.id = id;
        this.pw = pw;
        this.appPw = appPw;
    }

    @Override
    public String toString(){
        return "userID : " +
                id +
                ", " +
                "password : " +
                pw +
                ", " +
                "appPassword : " +
                appPw;
    }

}
