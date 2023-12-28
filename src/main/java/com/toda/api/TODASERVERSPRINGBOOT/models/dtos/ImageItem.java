package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import com.toda.api.TODASERVERSPRINGBOOT.providers.KafkaProducerProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

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
