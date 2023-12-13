package com.toda.api.TODASERVERSPRINGBOOT.models.fcms;

import lombok.*;

import java.util.Map;

@Getter
@ToString
public final class FcmMap {
    private Map<String,Long> tokenIDs;
    private Map<String,Integer> tokenStatus;

    public FcmMap(){}

    @Builder
    public FcmMap(
            Map<String,Long> tokenIDs,
            Map<String,Integer> tokenStatus
    ){
        this.tokenIDs = tokenIDs;
        this.tokenStatus = tokenStatus;
    }
}
