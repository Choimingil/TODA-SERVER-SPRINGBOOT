package com.fineapple.toda.api.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.util.List;

@ToString
public final class CommentListResponse {
    @JsonProperty("totalCommentNum")
    private int totalCommentNum;
    @JsonProperty("Comment")
    private List<CommentDetailResponse> comment;

    public List<CommentDetailResponse> getComment(){return this.comment;}

    public CommentListResponse(){}

    @Builder
    public CommentListResponse(int totalCommentNum, List<CommentDetailResponse> comment){
        this.totalCommentNum = totalCommentNum;
        this.comment = comment;
    }
}
