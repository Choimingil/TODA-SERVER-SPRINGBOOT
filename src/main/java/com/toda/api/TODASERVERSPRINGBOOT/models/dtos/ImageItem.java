package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ImageItem {
    private long imageID;
    private String URL;

    public ImageItem(){}

    @Builder
    public ImageItem(long imageID, String URL){
        this.imageID = imageID;
        this.URL = URL;
    }
}
