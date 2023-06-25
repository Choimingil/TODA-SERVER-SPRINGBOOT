package com.toda.api.TODASERVERSPRINGBOOT.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public enum RegularExpression {
    NUMBER(Pattern.compile("^[0-9]*$")),
    EMAIL(Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")),
    PASSWORD(Pattern.compile("^.*(?=^.{8,20}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$")),
    APP_PW(Pattern.compile("^[0-9]{1,4}$"));

    private final Pattern pattern;
}
