package com.toda.api.TODASERVERSPRINGBOOT.models.fcms;

import lombok.*;

@Getter
@ToString
public final class FcmIosNotification {
    private String title;
    private String body;
    private String type;
    private final String sound = "default";

    public FcmIosNotification(){}

    @Builder
    public FcmIosNotification(
            String title,
            String body,
            String type
    ){
        this.title = title;
        this.body = body;
        this.type = type;
    }
}


// diarypdo name, code, token, diaryNazme, status
// pushmsgpdo
// notiArray : body(푸시메시지 본문), title(푸시메시지 제목), sound, type(푸시메시지 종류 addDiaryFriend)
// dataArray : diaryID