package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class PostListResponse {
    private long diaryID;
    private long postID;
    private String name;
    private String title;
    private String date;
    private String mood;
    private int icon;
    private boolean isMyLike;
    private int likeNum;
    private int commentNum;

    public PostListResponse(){}

    @Builder
    public PostListResponse(
            long diaryID,
            long postID,
            String name,
            String title,
            String date,
            String mood,
            int icon,
            boolean isMyLike,
            int likeNum,
            int commentNum
    ){
        this.diaryID = diaryID;
        this.postID = postID;
        this.name = name;
        this.title = title;
        this.date = date;
        this.mood = mood;
        this.icon = icon;
        this.isMyLike = isMyLike;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }
}
