package com.toda.api.TODASERVERSPRINGBOOT.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public enum RegularExpressions {
    NUMBER(Pattern.compile("^[0-9]*$")),
    EMAIL(Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")),
    APP_PW(Pattern.compile("^[0-9]{1,4}$"));

    private final Pattern pattern;
}
