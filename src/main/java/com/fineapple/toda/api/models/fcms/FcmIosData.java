package com.fineapple.toda.api.models.fcms;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class FcmIosData {
    private long data;

    public FcmIosData(){}

    @Builder
    public FcmIosData(long data){
        this.data = data;
    }
}
