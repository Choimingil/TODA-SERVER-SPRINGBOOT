package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public final class FcmDto {
    private String title;
    private String body;
    private int typeNum;
    private long dataID;
    private Map<Long,String> map;

    public FcmDto(){}

    @Builder
    public FcmDto(
            String title,
            String body,
            int typeNum,
            long dataID,
            Map<Long,String> map
    ){
        this.title = title;
        this.body = body;
        this.typeNum = typeNum;
        this.dataID = dataID;
        this.map = map;
    }
}
