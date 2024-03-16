package com.fineapple.toda.api.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenFields {
    USER_ID("userID"),
    USER_CODE("userCode"),
    EMAIL("email"),
    PASSWORD("password"),
    USER_NAME("userName"),
    APP_PASSWORD("appPassword"),
    CREATE_AT("createAt"),
    PROFILE("profile");

    public final String value;
}