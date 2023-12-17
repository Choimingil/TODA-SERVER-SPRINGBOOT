package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class DiaryMemberListResponse {
    private long diaryID;
    private String name;
    private long userID;
    private String userName;
    private String userSelfie;
    private int userNum;

    public DiaryMemberListResponse(){}

    @Builder
    public DiaryMemberListResponse(
            long diaryID,
            String name,
            long userID,
            String userName,
            String userSelfie,
            int userNum
    ){
        this.diaryID = diaryID;
        this.name = name;
        this.userName = userName;
        this.userID = userID;
        this.userSelfie = userSelfie;
        this.userNum = userNum;
    }
}