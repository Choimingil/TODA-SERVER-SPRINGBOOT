package com.toda.api.TODASERVERSPRINGBOOT.models.bodies;

import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidUrl;
import com.toda.api.TODASERVERSPRINGBOOT.validators.annotations.ValidUserName;
import com.toda.api.TODASERVERSPRINGBOOT.providers.TokenProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UpdateUser {
    @ValidUserName private String name;
    @ValidUrl private String image;
    public UpdateUser(){}

    @Builder
    public UpdateUser(String name, String image){
        if(name == null) this.name = TokenProvider.SKIP_VALUE;
        else this.name = name;

        if(image == null) this.image = TokenProvider.SKIP_VALUE;
        else this.image = image;
    }
}
