package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidTime;
import lombok.*;

@Getter
@ToString
public final class UpdateFcmTime {
    @ValidTime
    private String time;
    private String fcmToken;

    public UpdateFcmTime(){}

    @Builder
    public UpdateFcmTime(String time, String fcmToken){
        this.time = time;
        this.fcmToken = fcmToken;
    }
}
