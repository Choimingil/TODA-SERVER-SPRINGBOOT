package com.fineapple.toda.api.abstracts.interfaces;

import com.fineapple.toda.api.entities.User;
import com.fineapple.toda.api.entities.mappings.UserDetail;

public interface BaseUserAuth {
    /**
     * 외부 클래스에서 Redis에 저장된 유저 정보 Get 시 사용
     * @param value : email or token (문자열 길이로 구별)
     * @return : User type
     */
    UserDetail getUserInfo(String value);

    /**
     * MDC 및 Redis 정보 추가
     * @param user
     * @param profile
     */
    void updateUserRedis(User user, String profile);

    /**
     * MDC 및 Redis에 저장되어 있는 유저 정보 삭제
     * @param email
     */
    void deleteUserInfo(String email);
}
