package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class AddSticker {
    private List<AddStickerDetail> stickerArr;

    public AddSticker(){}

    @Builder
    public AddSticker(List<AddStickerDetail> stickerArr){
        this.stickerArr = stickerArr;
    }
}
