package com.toda.api.TODASERVERSPRINGBOOT.models.Fcms;

import lombok.*;

@Getter
@ToString
public final class FcmAosData {
    private String title;
    private String body;
    private String type;
    private long data;

    public FcmAosData(){}

    @Builder
    public FcmAosData(
            String title,
            String body,
            String type,
            long data
    ){
        this.title = title;
        this.body = body;
        this.type = type;
        this.data = data;
    }
}
