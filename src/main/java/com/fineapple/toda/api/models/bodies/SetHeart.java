package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.ValidMood;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class SetHeart {
    @ValidMood private int mood;

    public SetHeart(){}

    @Builder
    public SetHeart(int mood){
        this.mood = mood;
    }
}
