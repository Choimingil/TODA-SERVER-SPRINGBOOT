package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PostNotice {
    private long diary;
    private String notice;

    public PostNotice(){}

    @Builder
    public PostNotice(long diary, String notice){
        this.diary = diary;
        this.notice = notice;
    }
}
