package com.fineapple.toda.api.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
public final class PostImageResponse {
    @JsonProperty("imageID")
    private long imageID;
    @JsonProperty("URL")
    private String url;

    public PostImageResponse(){}

    @Builder
    public PostImageResponse(long imageID, String url){
        this.imageID = imageID;
        this.url = url;
    }
}
