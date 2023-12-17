package com.toda.api.TODASERVERSPRINGBOOT.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public enum RegularExpressions {
    NUMBER(Pattern.compile("^[0-9]*$")),
    EMAIL(Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")),
    USER_CODE(Pattern.compile("^[0-9A-Z]{9}$")),
    DATE(Pattern.compile("^(\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")),
    TIME(Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$")),
    APP_PW(Pattern.compile("^[0-9]{1,4}$"));

    private final Pattern pattern;
}
