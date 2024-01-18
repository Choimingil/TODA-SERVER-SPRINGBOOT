package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
public final class UserLogResponse {
    @JsonProperty("type")
    private long type;
    @JsonProperty("ID")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("selfie")
    private String selfie;
    @JsonProperty("image")
    private String image;
    @JsonProperty("date")
    private String date;
    @JsonProperty("isReplied")
    private boolean isReplied;

    UserLogResponse(){}

    @Builder
    UserLogResponse(
            long type,
            long id,
            String name,
            String selfie,
            String image,
            String date,
            boolean isReplied
    ){
        this.type = type;
        this.id = id;
        this.name = name;
        this.selfie = selfie;
        this.image = image;
        this.date = date;
        this.isReplied = isReplied;
    }
}
