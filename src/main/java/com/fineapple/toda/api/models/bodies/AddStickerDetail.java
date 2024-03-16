package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class AddStickerDetail {
    private long stickerID;
    private int device;
    private double x;
    private double y;
    private StickerRotate rotate;
    private StickerScale scale;
    private int inversion;
    private int layerNum;

    public AddStickerDetail(){}

    @Builder
    public AddStickerDetail(
            long stickerID,
            int device,
            double x,
            double y,
            StickerRotate rotate,
            StickerScale scale,
            int inversion,
            int layerNum
    ){
        this.stickerID = stickerID;
        this.device = device;
        this.x = x;
        this.y = y;
        this.rotate = rotate;
        this.scale = scale;
        this.inversion = inversion;
        this.layerNum = layerNum;
    }
}
