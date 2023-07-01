package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class DecodeTokenResponseDto extends AbstractModel implements BaseModel {
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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("userID : ");
        sb.append(id);
        sb.append(", ");
        sb.append("password : ");
        sb.append(pw);
        sb.append(", ");
        sb.append("appPassword : ");
        sb.append(appPw);
        return sb.toString();
    }

    public Map<String, ?> toMap() {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("pw",pw);
        map.put("appPw",appPw);
        return map;
    }
}
