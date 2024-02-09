package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public final class JwtHeader {
    private long userID;
    private String email;
    private LocalDateTime date;
    private int appPw;

    JwtHeader(){}

    @Builder
    JwtHeader(long userID, String email, LocalDateTime date, int appPw){
        this.userID = userID;
        this.email = email;
        this.date = date;
        this.appPw = appPw;
    }
}
