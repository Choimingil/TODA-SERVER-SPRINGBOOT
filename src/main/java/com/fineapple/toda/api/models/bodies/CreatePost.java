package com.fineapple.toda.api.models.bodies;

import com.fineapple.toda.api.validators.annotations.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@ToString
public final class CreatePost {
    private long diary;
    @ValidDate
    private String date;
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

    public CreatePost(){}

    @Builder
    public CreatePost(
            long diary,
            String date,
            String title,
            String text,
            int mood,
            int background,
            int aligned,
            int font,
            List<String> imageList
    ){
        this.diary = diary;
        this.title = title;
        this.text = text;
        this.mood = mood;
        this.background = background;
        this.aligned = aligned;
        this.font = font;

        if(date != null) this.date = date;
        else{
            LocalDateTime curr = LocalDateTime.now();
            StringBuilder sb = new StringBuilder();
            sb.append(curr.getYear()).append("-");

            if(curr.getMonthValue()<10) sb.append("0");
            sb.append(curr.getMonthValue()).append("-");

            if(curr.getDayOfMonth()<10) sb.append("0");
            sb.append(curr.getDayOfMonth());

            this.date = sb.toString();
        }

        if(imageList != null) this.imageList = imageList;
        else this.imageList = new ArrayList<>();
    }
}
