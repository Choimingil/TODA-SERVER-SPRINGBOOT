package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
public final class DecodeTokenResponseDTO {
    long id;
    @NotNull String pw;
    int appPw;

    public DecodeTokenResponseDTO(){}

    @Builder
    public DecodeTokenResponseDTO(
            long id,
            @NotNull String pw,
            int appPw
    ){
        this.id = id;
        this.pw = pw;
        this.appPw = appPw;
    }

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
