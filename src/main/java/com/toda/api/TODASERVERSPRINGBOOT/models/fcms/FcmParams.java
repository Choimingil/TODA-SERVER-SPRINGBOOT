package com.toda.api.TODASERVERSPRINGBOOT.models.fcms;

import lombok.*;

@Getter
@ToString
public final class FcmParams {
    private String title;
    private String body;
    private int typeNum;
    private long dataID;
    FcmGroup fcmGroup;

    public FcmParams(){}

    @Builder
    public FcmParams(
            String title,
            String body,
            int typeNum,
            long dataID,
            FcmGroup fcmGroup
    ){
        this.title = title;
        this.body = body;
        this.typeNum = typeNum;
        this.dataID = dataID;
        this.fcmGroup = fcmGroup;
    }

}
