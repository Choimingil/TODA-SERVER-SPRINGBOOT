package com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses;

public class LoginResponseDTO extends DefaultResponseDTO {
    public String result;

    // 테스트 시 결과값 매핑을 위한 추가
    public LoginResponseDTO(){

    }

    public LoginResponseDTO(int code, String message, String result) {
        super(code, message);
        this.result = result;
    }
}