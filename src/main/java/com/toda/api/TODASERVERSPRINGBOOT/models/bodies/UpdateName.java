package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidUserName;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdateName {
    @ValidUserName private String name;
    public UpdateName(){}
    @Builder
    public UpdateName(String name){
        this.name = name;
    }
}
