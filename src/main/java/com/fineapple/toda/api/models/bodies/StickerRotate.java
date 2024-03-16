package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StickerRotate {
    private double a;
    private double b;
    private double c;
    private double d;
    private double tx;
    private double ty;

    public StickerRotate(){}

    @Builder
    public StickerRotate(double a,double b,double c,double d,double tx,double ty){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.tx = tx;
        this.ty = ty;
    }
}
