package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class StickerScale {
    private double x;
    private double y;
    private double width;
    private double height;

    public StickerScale(){}

    @Builder
    public StickerScale(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
