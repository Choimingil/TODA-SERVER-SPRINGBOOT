package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ReCommentDetailResponse {
    private long parent;
    private long userID;
    private String userName;
    private String userSelfie;
    private long commentID;
    private String comment;
    private String time;
    private boolean isMyComment;

    public boolean getIsMyComment(){return isMyComment;}

    public ReCommentDetailResponse(){}

    @Builder
    public ReCommentDetailResponse(
            long parent,
            long userID,
            String userName,
            String userSelfie,
            long commentID,
            String comment,
            String time,
            boolean isMyComment
    ){
        this.parent = parent;
        this.userID = userID;
        this.userName = userName;
        this.userSelfie = userSelfie;
        this.commentID = commentID;
        this.comment = comment;
        this.time = time;
        this.isMyComment = isMyComment;
    }
}
