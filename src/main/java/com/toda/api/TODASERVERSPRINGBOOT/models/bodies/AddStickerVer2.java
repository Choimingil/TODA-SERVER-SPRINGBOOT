package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class AddStickerVer2 {
    private long post;
    private List<AddStickerDetail> stickerArr;

    public AddStickerVer2(){}

    @Builder
    public AddStickerVer2(long post, List<AddStickerDetail> stickerArr){
        this.post = post;
        this.stickerArr = stickerArr;
    }
}
