package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;

public interface BaseUserAuth {
    /**
     * 외부 클래스에서 Redis에 저장된 유저 정보 Get 시 사용
     * @param value : email or token (문자열 길이로 구별)
     * @return : User type
     */
    UserData getUserInfo(String value);

    /**
     * MDC 및 Redis 정보 추가
     * @param userData
     */
    void setUserInfo(UserData userData);

    /**
     * MDC 및 Redis에 저장되어 있는 유저 정보 삭제
     * @param email
     */
    void deleteUserInfo(String email);
}
