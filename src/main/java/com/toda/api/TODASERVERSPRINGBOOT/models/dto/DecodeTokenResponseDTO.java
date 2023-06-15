package com.toda.api.TODASERVERSPRINGBOOT.models.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DecodeTokenResponseDTO {
    public Long id;
    public String pw;
    public int appPw;

//    // 테스트 시 결과값 매핑을 위한 추가
//    public DecodeTokenResponseDTO(){
//
//    }
//    public DecodeTokenResponseDTO(
//            Long id,
//            String pw,
//            int appPw
//    ) {
//        this.id = id;
//        this.pw = pw;
//        this.appPw = appPw;
//    }
}
