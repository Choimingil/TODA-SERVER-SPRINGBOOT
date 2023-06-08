package com.toda.api.TODASERVERSPRINGBOOT.models.dto.responses;

public class DefaultResponseDTO {
    public boolean isSuccess;
    public int code;
    public String message;

    // 테스트 시 결과값 매핑을 위한 추가
    public DefaultResponseDTO(){

    }

    public DefaultResponseDTO(int code, String message){
        this.isSuccess = code == 100 || code == 200;
        this.code = code;
        this.message = message;
    }
}
