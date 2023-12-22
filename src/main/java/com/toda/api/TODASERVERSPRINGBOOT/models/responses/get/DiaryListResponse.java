package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public final class DiaryListResponse {
    private long diaryID;
    private String userName;
    private String name;
    private int status;
    private int color;
    private int colorCode;
    private int userNum;

    public DiaryListResponse(){}

    @Builder
    public DiaryListResponse(
            long diaryID,
            String userName,
            String name,
            int status,
            int color,
            int colorCode,
            int userNum
    ){
        this.diaryID = diaryID;
        this.userName = userName;
        this.name = name;
        this.status = status;
        this.color = color;
        this.colorCode = colorCode;
        this.userNum = userNum;
    }
}
