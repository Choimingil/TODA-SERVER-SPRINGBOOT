package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import com.toda.api.TODASERVERSPRINGBOOT.entities.DiaryNotice;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class DiaryNoticeResponse {
    private long diaryID;
    private String diaryName;
    private long userID;
    private String userName;
    private String notice;
    private long date;

    public DiaryNoticeResponse(){}

    @Builder
    public DiaryNoticeResponse(
            long diaryID,
            String diaryName,
            long userID,
            String userName,
            String notice,
            long date
    ){
        this.diaryID = diaryID;
        this.diaryName = diaryName;
        this.userID = userID;
        this.userName = userName;
        this.notice = notice;
        this.date = date;
    }
}
