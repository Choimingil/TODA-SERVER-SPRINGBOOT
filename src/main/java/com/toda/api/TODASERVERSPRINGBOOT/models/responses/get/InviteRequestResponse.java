package com.toda.api.TODASERVERSPRINGBOOT.models.responses.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public final class InviteRequestResponse {
    private long userID;
    private String userCode;
    private String email;
    private String name;
    private String birth;
    private String selfie;
    private long diaryID;
    private String diaryName;
    private String date;

    InviteRequestResponse(){}

    @Builder
    InviteRequestResponse(
            long userID,
            String userCode,
            String email,
            String name,
            String selfie,
            long diaryID,
            String diaryName,
            String date
    ){
        this.userID = userID;
        this.userCode = userCode;
        this.email = email;
        this.name = name;
        this.selfie = selfie;
        this.diaryID = diaryID;
        this.diaryName = diaryName;
        this.date = date;

        LocalDateTime curr = LocalDateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append(curr.getYear()).append("-");
        if(curr.getMonthValue()<10) sb.append("0");
        sb.append(curr.getMonthValue()).append("-");
        if(curr.getDayOfMonth()<10) sb.append("0");
        sb.append(curr.getDayOfMonth());
        this.birth = sb.toString();
    }
}
