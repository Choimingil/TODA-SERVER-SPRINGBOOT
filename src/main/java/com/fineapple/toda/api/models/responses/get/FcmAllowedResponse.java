package com.fineapple.toda.api.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@ToString
public final class FcmAllowedResponse {
    @JsonProperty("isBasicAllowed")
    private boolean isBasicAllowed;
    @JsonProperty("isRemindAllowed")
    private boolean isRemindAllowed;
    @JsonProperty("isEventAllowed")
    private boolean isEventAllowed;

    FcmAllowedResponse(){}

    @Builder
    FcmAllowedResponse(boolean isBasicAllowed, boolean isRemindAllowed, boolean isEventAllowed){
        this.isBasicAllowed = isBasicAllowed;
        this.isRemindAllowed = isRemindAllowed;
        this.isEventAllowed = isEventAllowed;
    }
}
