package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.annotations.ValidTitle;
import lombok.*;

@Getter
@ToString
public final class CreateDiary {
    private int status;
    @ValidTitle private String title;
    private int color;

    public CreateDiary(){}

    @Builder
    public CreateDiary(int status, String title, int color){
        this.status = status;
        this.title = title;
        this.color = color;
    }
}
