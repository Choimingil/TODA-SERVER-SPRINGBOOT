package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public final class DecodeTokenResponseDto {
    private long id;
    @NonNull private String pw;
    @NonNull private int appPw;

    public DecodeTokenResponseDto(){}

    @Builder
    public DecodeTokenResponseDto(
            long id,
            @NonNull String pw,
            @NonNull int appPw
    ){
        this.id = id;
        this.pw = pw;
        this.appPw = appPw;
    }

    public Map<String, ?> toMap() {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("pw",pw);
        map.put("appPw",appPw);
        return map;
    }
}
