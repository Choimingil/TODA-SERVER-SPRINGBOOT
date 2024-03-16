package com.fineapple.toda.api.models.fcms;

import lombok.*;

import java.util.List;

@Getter
@ToString
public final class FcmIos {
    private List<String> registration_ids;
    private FcmIosNotification notification;
    private FcmIosData data;

    public FcmIos(){}

    @Builder
    public FcmIos(
            List<String> registration_ids,
            FcmIosNotification notification,
            FcmIosData data
    ){
        this.registration_ids = registration_ids;
        this.notification = notification;
        this.data = data;
    }
}
