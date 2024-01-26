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

    LoginResponse(){}

    @Builder
    LoginResponse(
            String jwt,
            boolean isUpdating
    ){
        this.jwt = jwt;
        this.isUpdating = isUpdating;
    }

    @ToString
    static class UpdateTime{
        @JsonProperty("startTime")
        private String startTime;
        @JsonProperty("finishTime")
        private String finishTime;

        UpdateTime(){}

        @Builder
        UpdateTime(String startTime, String finishTime){
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
    }
}
