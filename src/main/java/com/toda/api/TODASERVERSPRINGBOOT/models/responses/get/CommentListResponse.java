package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class CommentListResponse {
    private int totalCommentNum;
    @JsonProperty("Comment")
    private List<CommentDetailResponse> comment;

    public CommentListResponse(){}

    @Builder
    public CommentListResponse(int totalCommentNum, List<CommentDetailResponse> comment){
        this.totalCommentNum = totalCommentNum;
        this.comment = comment;
    }
}
