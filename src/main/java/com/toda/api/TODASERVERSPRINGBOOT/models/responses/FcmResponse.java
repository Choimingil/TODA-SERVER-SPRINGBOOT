package com.toda.api.TODASERVERSPRINGBOOT.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public final class FcmResponse {
    @JsonProperty("multicast_id")
    private long multicastId;
    private int success;
    private int failure;
    @JsonProperty("canonical_ids")
    private long canonicalIds;
    private List<Result> results;

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static final class Result{
        @JsonProperty("message_id")
        private String messageId;

        @JsonProperty("error")
        private String error;
    }
}
