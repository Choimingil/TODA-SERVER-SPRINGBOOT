package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class RegistrationId {
    @JsonProperty("type")
    private String type;
    @JsonProperty("token")
    private String token;

    RegistrationId(){}

    @Builder
    public RegistrationId(String type, String token){
        this.type = type;
        this.token = token;
    }
}
