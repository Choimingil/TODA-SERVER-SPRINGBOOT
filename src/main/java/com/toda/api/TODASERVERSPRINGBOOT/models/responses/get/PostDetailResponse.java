package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.ImageItem;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public final class PostDetailResponse {
    private boolean isMyPost;
    private long diaryID;
    private String name;
    private long postID;
    private String date;
    private String dateFull;
    private String title;
    private String text;
    private int background;
    private String mood;
    private int icon;
    private int aligned;
    private int font;
    private List<ImageItem> image;
    private boolean isMyLike;
    private int likeNum;
    private int commentNum;


    public PostDetailResponse(){}

    @Builder
    public PostDetailResponse(
            boolean isMyPost,
            long diaryID,
            String name,
            long postID,
            String date,
            String dateFull,
            String title,
            String text,
            int background,
            String mood,
            int icon,
            int aligned,
            int font,
            List<ImageItem> image,
            boolean isMyLike,
            int likeNum,
            int commentNum
    ){
        this.isMyPost = isMyPost;
        this.diaryID = diaryID;
        this.name = name;
        this.postID = postID;
        this.date = date;
        this.dateFull = dateFull;
        this.title = title;
        this.text = text;
        this.background = background;
        this.mood = mood;
        this.icon = icon;
        this.aligned = aligned;
        this.font = font;
        this.image = image;
        this.isMyLike = isMyLike;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }
}
