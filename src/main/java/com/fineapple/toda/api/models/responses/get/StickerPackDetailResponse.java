package com.fineapple.toda.api.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class StickerPackDetailResponse {
    private long stickerPackID;
    private String name;
    private int point;
    private List<StickerDetailResponse> stickerArr;

    public StickerPackDetailResponse(){}

    @Builder
    public StickerPackDetailResponse(long stickerPackID, String name, int point, List<StickerDetailResponse> stickerArr){
        this.stickerPackID = stickerPackID;
        this.name = name;
        this.point = point;
        this.stickerArr = stickerArr;
    }
}
