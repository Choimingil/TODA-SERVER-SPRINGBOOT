package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public final class UpdatePost {
    private long post;
    @ValidDate private String date;
    @ValidTitle
    private String title;
    private String text;
    @ValidMood private int mood;
    @ValidBackground
    private int background;
    @ValidAligned
    private int aligned;
    @ValidFont
    private int font;
    @ValidImageList
    private List<String> imageList;

    public UpdatePost(){}

    @Builder
    public UpdatePost(
            long post,
            String date,
            String title,
            String text,
            int mood,
            int background,
            int aligned,
            int font,
            List<String> imageList
    ){
        this.post = post;
        this.title = title;
        this.text = text;
        this.mood = mood;
        this.background =background;
        this.aligned = aligned;
        this.font = font;

        if(date != null) this.date = date;
        else this.date = "1901-01-01";

        if(imageList != null) this.imageList = imageList;
        else this.imageList = new ArrayList<>();
    }
}
