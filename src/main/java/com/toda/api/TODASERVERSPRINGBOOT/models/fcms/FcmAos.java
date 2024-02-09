package com.toda.api.TODASERVERSPRINGBOOT.models.fcms;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class FcmAos {
    private List<String> registration_ids;
    private FcmAosData data;

    public FcmAos(){}

    @Builder
    public FcmAos(
            List<String> registration_ids,
            FcmAosData data
    ){
        this.registration_ids = registration_ids;
        this.data = data;
    }
}
