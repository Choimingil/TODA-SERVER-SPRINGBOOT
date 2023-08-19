package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import lombok.*;

@Getter
@ToString
public final class UpdateFcmTime {
    private String time;
    private String fcmToken;

    public UpdateFcmTime(){}

    @Builder
    public UpdateFcmTime(String time, String fcmToken){
        this.time = time;
        this.fcmToken = fcmToken;
    }
}
