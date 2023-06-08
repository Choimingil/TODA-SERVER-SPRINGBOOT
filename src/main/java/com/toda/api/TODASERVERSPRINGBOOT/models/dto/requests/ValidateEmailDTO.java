package com.toda.api.TODASERVERSPRINGBOOT.models.dto.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ValidateEmailDTO extends DefaultRequestDTO{
    public String email;

    // 테스트 시 비교를 위한 코드 추가
    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(this.email));
    }
}
