package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class UpdateSticker {
    private List<UpdateStickerDetail> usedStickerArr;

    public UpdateSticker(){}

    @Builder
    public UpdateSticker(List<UpdateStickerDetail> usedStickerArr){
        this.usedStickerArr = usedStickerArr;
    }
}
