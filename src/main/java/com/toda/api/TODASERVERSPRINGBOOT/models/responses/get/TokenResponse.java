package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
public final class TokenResponse {
    @JsonProperty("date")
    private String date;
    @JsonProperty("id")
    private long id;
    @JsonProperty("appPW")
    private int appPW;
    @JsonProperty("email")
    private String email;
    @JsonProperty("code")
    private String code;

    TokenResponse(){}

    @Builder
    TokenResponse(String date, long id, int appPW, String email, String code){
        this.date = date;
        this.id = id;
        this.appPW = appPW;
        this.email = email;
        this.code = code;
    }
}
