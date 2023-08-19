package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import lombok.*;

@Getter
@ToString
public final class UpdateFcmAllowed {
    private int alarmType;
    private String fcmToken;

    public UpdateFcmAllowed(){}

    @Builder
    public UpdateFcmAllowed(int alarmType, String fcmToken){
        this.alarmType = alarmType;
        this.fcmToken = fcmToken;
    }
}
