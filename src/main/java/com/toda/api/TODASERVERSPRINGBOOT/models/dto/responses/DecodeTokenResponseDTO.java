package com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses;

public class DecodeTokenResponseDTO extends DefaultResponseDTO {
    public Long id;
    public String pw;
    public int appPw;

    // 테스트 시 결과값 매핑을 위한 추가
    public DecodeTokenResponseDTO(){

    }
    public DecodeTokenResponseDTO(
            int code,
            String message,
            Long id,
            String pw,
            int appPw
    ) {
        super(code, message);
        this.id = id;
        this.pw = pw;
        this.appPw = appPw;
    }
}
