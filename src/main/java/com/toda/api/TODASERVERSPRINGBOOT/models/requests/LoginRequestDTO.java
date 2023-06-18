package com.toda.api.TODASERVERSPRINGBOOT.models.requests;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequestDTO {
    public String id;
    public String pw;

    // 테스트 시 비교를 위한 코드 추가
    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(this.id));
    }
}
