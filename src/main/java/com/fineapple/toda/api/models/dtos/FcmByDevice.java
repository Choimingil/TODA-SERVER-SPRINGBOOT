package com.fineapple.toda.api.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class FcmByDevice {
    private String token;
    private int device;

    FcmByDevice(){}

    @Builder
    FcmByDevice(String token, int device){
        this.token = token;
        this.device = device;
    }
}
