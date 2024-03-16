package com.fineapple.toda.api.models.bodies;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdateComment {
    private long comment;
    private String reply;

    public UpdateComment(){}

    @Builder
    public UpdateComment(long comment, String reply){
        this.comment = comment;
        this.reply = reply;
    }
}
