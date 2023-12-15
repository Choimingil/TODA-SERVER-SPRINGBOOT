package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdateNotice {
    private long diary;
    private String notice;

    public UpdateNotice(){}

    @Builder
    public UpdateNotice(long diary, String notice){
        this.diary = diary;
        this.notice = notice;
    }
}
