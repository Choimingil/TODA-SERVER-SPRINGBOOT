package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class DecodeTokenResponseDTO {
    public Long id;
    public String pw;
    public int appPw;

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
