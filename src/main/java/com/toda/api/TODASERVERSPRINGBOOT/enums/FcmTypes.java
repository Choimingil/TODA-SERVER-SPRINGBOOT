package com.toda.api.TODASERVERSPRINGBOOT.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FcmTypes {
    TYPE_1("addDiaryFriend"),
    TYPE_2("acceptDiaryFriend"),
    TYPE_3("addPost"),
    TYPE_4("postLike"),
    TYPE_5("postComment"),
    TYPE_6("postComment");

    public final String type;
}
