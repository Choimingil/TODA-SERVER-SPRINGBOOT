package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import com.toda.api.TODASERVERSPRINGBOOT.utils.validations.annotations.ValidAppPw;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class CheckTokenDTO {
    @ValidAppPw
    public String appPW;
}
