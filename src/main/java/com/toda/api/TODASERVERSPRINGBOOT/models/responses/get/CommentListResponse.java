package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class CommentListResponse {
    private int totalCommentNum;
    private List<CommentDetailResponse> comment;

    public CommentListResponse(){}

    @Builder
    public CommentListResponse(int totalCommentNum, List<CommentDetailResponse> comment){
        this.totalCommentNum = totalCommentNum;
        this.comment = comment;
    }
}
