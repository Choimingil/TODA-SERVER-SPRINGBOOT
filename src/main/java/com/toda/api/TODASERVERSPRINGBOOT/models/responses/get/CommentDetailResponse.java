package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public final class CommentDetailResponse {
    private long postID;
    private long userID;
    private String userName;
    private String userSelfie;
    private long commentID;
    private String comment;
    private String time;
    private boolean isMyComment;
    private final List<ReCommentDetailResponse> reComment = new ArrayList<>();

    public boolean getIsMyComment(){return isMyComment;}

    public CommentDetailResponse(){}

    @Builder
    public CommentDetailResponse(
            long postID,
            long userID,
            String userName,
            String userSelfie,
            long commentID,
            String comment,
            String time,
            boolean isMyComment
    ){
        this.postID = postID;
        this.userID = userID;
        this.userName = userName;
        this.userSelfie = userSelfie;
        this.commentID = commentID;
        this.comment = comment;
        this.time = time;
        this.isMyComment = isMyComment;
    }
}
