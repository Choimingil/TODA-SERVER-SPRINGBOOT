package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
public final class LoginResponse {
    @JsonProperty("jwt")
    private String jwt;
    @JsonProperty("isUpdating")
    private boolean isUpdating;
    @JsonProperty("startTime")
    private final boolean startTime = false;
    @JsonProperty("finishTime")
    private final boolean finishTime = false;

    LoginResponse(){}

    @Builder
    LoginResponse(
            String jwt,
            boolean isUpdating
    ){
        this.jwt = jwt;
        this.isUpdating = isUpdating;
    }
}
