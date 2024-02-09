package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.StickerRotate;
import com.toda.api.TODASERVERSPRINGBOOT.models.bodies.StickerScale;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PostStickerListResponse {
    private long postID;
    private long usedStickerID;
    private long userID;
    private long stickerID;
    private String image;
    private int device;
    private double x;
    private double y;
    private StickerRotate rotate;
    private StickerScale scale;
    private int inversion;
    private int layerNum;
    private boolean isMySticker;

    public boolean getIsMySticker(){return isMySticker;}

    public PostStickerListResponse(){}

    @Builder
    public PostStickerListResponse(
            long postID,
            long usedStickerID,
            long userID,
            long stickerID,
            String image,
            int device,
            double x,
            double y,
            StickerRotate rotate,
            StickerScale scale,
            int inversion,
            int layerNum,
            boolean isMySticker
    ){
        this.postID = postID;
        this.usedStickerID = usedStickerID;
        this.userID = userID;
        this.stickerID = stickerID;
        this.image = image;
        this.device = device;
        this.x = x;
        this.y = y;
        this.rotate = rotate;
        this.scale = scale;
        this.inversion = inversion;
        this.layerNum = layerNum;
        this.isMySticker = isMySticker;
    }
}
