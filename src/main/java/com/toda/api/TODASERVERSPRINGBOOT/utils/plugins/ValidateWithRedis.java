package com.toda.api.TODASERVERSPRINGBOOT.utils.plugins;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.ValueOperations;

public interface ValidateWithRedis {
    /**
     * Claims를 매개변수로 Redis에 값이 존재하는지 체크
     * @param claims
     * @return
     */
    default boolean isExistRedis(Claims claims){
        return getValueOperations().get(claims.getSubject()) != null;
    }

    /**
     * Redis key를 매개변수로 Redis에 값이 존재하는지 체크
     * @param key
     * @return
     */
    default boolean isExistRedis(String key){
        return getValueOperations().get(key) != null;
    }

    /**
     * DB 정보와 토큰값과 같은지 체크
     * @param claims
     * @return
     */
    default boolean isEqualWithDB(Claims claims){
        UserInfoAllDao userInfoAllDao = getRepository().getUserInfoAll(claims.getSubject());
        if(userInfoAllDao.isSameTokenAttributes(claims)){
            getValueOperations().set(claims.getSubject(),userInfoAllDao);
            return true;
        }
        else return false;
    }

    /**
     * Claims를 매개변수로 Redis에 저장된 값 get
     * @param claims
     * @return
     * @param <T>
     */
    default <T> T getRedis(Claims claims){
        // 타입 형 변환 제네릭 더 공부하면서 방법 찾아보기
        @SuppressWarnings ("unchecked") T res = (T) getValueOperations().get(claims.getSubject());
        return res;
    }

    /**
     * Redis key를 매개변수로 Redis에 저장된 값 get
     * @param key
     * @return
     * @param <T>
     */
    default <T> T getRedis(String key){
        // 타입 형 변환 제네릭 더 공부하면서 방법 찾아보기
        @SuppressWarnings ("unchecked") T res = (T) getValueOperations().get(key);
        return res;
    }

    /**
     * Claims를 매개변수로 Redis에 DB값 저장
     * @param claims
     */
    default void setRedis(Claims claims){
        UserInfoAllDao userInfoAllDao = getRepository().getUserInfoAll(claims.getSubject());
        getValueOperations().set(claims.getSubject(),userInfoAllDao);
    }

    /**
     * Redis key 매개변수로 Redis에 DB값 저장
     * @param key
     */
    default void setRedis(String key){
        UserInfoAllDao userInfoAllDao = getRepository().getUserInfoAll(key);
        getValueOperations().set(key,userInfoAllDao);
    }

    /**
     * redisTemplate.opsForValue() getter
     * @return
     */
    ValueOperations<String, Object> getValueOperations();

    /**
     * AuthRepository getter
     * @return
     */
    AuthRepository getRepository();

}
