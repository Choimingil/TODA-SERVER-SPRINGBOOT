package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class CommentListResponse {
    private int totalCommentNum;
    private List<CommentDetailResponse> Comment;

    public CommentListResponse(){}

    @Builder
    public CommentListResponse(int totalCommentNum, List<CommentDetailResponse> Comment){
        this.totalCommentNum = totalCommentNum;
        this.Comment = Comment;
    }
}
