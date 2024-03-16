package com.fineapple.toda.api.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class StickerDetailResponse {
    private long stickerID;
    private String image;

    public StickerDetailResponse(){}

    @Builder
    public StickerDetailResponse(long stickerID, String image){
        this.stickerID = stickerID;
        this.image = image;
    }
}
