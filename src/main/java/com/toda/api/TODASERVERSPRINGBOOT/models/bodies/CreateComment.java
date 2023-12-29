package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidTitle;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CreateComment {
    private long post;
    private String reply;

    public CreateComment(){}

    @Builder
    public CreateComment(long post, String reply){
        this.post = post;
        this.reply = reply;
    }
}
