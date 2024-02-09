package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.RegistrationId;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class FcmResponse {
    @JsonProperty("registration_ids")
    private List<RegistrationId> registration_ids;
    @JsonProperty("body")
    private String body;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("data")
    private Object data;

    FcmResponse(){}

    @Builder
    FcmResponse(List<RegistrationId> registration_ids, String body, String title, String type, Object data){
        this.registration_ids = registration_ids;
        this.body = body;
        this.title = title;
        this.type = type;
        this.data = data;
    }
}
