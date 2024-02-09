package com.toda.api.TODASERVERSPRINGBOOT.models.fcms;

import lombok.*;

import java.util.List;

@Getter
@ToString
public final class FcmGroup {
    private List<String> aosFcmList;
    private List<String> iosFcmList;

    public FcmGroup(){}

    @Builder
    public FcmGroup(
            List<String> aosFcmList,
            List<String> iosFcmList
    ){
        this.aosFcmList = aosFcmList;
        this.iosFcmList = iosFcmList;
    }
}
