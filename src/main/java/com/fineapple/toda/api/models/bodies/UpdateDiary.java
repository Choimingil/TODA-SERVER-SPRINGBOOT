package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdateDiary {
    private long diary;
    private int status;
    @ValidTitle
    private String title;
    private int color;

    public UpdateDiary(){}

    @Builder
    public UpdateDiary(long diary, int status, String title, int color){
        this.diary = diary;
        this.status = status;
        this.title = title;
        this.color = color;
    }
}
