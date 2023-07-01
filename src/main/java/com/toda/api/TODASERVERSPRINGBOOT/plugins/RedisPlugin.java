package com.toda.api.TODASERVERSPRINGBOOT.plugins;

import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.ValueOperations;

public interface RedisPlugin {
    /**
     * Redis에 값이 존재하는지 체크
     * @param obj
     * @param clazz
     * @return
     * @param <T>
     */
    default <T> boolean isExistRedis(T obj, Class<T> clazz) {
        String key = "";
        if(clazz == Claims.class){
            Claims claims = (Claims) obj;
            key = claims.getSubject();
        }
        else if(clazz == String.class) key = (String) obj;

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
    default <T> T getRedisWithClaims(Claims claims, Class<T> c){
        if(c.isInstance(getValueOperations().get(claims.getSubject()))){
            @SuppressWarnings ("unchecked") T res = (T) getValueOperations().get(claims.getSubject());
            return res;
        }
        else throw new WrongArgException(WrongArgException.of.WRONG_TYPE_EXCEPTION);
    }

    /**
     * Redis key를 매개변수로 Redis에 저장된 값 get
     * @param key
     * @return
     * @param <T>
     */
    default <T> T getRedisWithKey(String key, Class<T> c){
        if(c.isInstance(getValueOperations().get(key))){
            @SuppressWarnings ("unchecked") T res = (T) getValueOperations().get(key);
            return res;
        }
        else throw new WrongArgException(WrongArgException.of.WRONG_TYPE_EXCEPTION);
    }

    /**
     *
     * class.isInstance : 이 클래스가 특정 클래스 또는 그 클래스의 하위 클래스인지 여부를 확인할 때 사용
     * 장점 : 비교 클래스가 클래스 리터럴 상태가 아니어도 비교 가능
     * 단점 : 정적으로 컴파일 시간에 알려진 클래스일 경우 클래스 리터럴을 이용해서 비교 불가
     * == : 두 클래스가 같은지 확인
     * 장점 : 클래스 리터럴을 이용해서 타입 비교 가능
     * 단점 : 클래스 리터럴 상태가 아닐 경우 비교 불가
     * @param obj
     * @param clazz
     * @param <T>
     */
    default <T> void setRedis(T obj, Class<T> clazz){
        String key = "";
        if(clazz == Claims.class){
            Claims claims = (Claims) obj;
            key = claims.getSubject();
        }
        else if(clazz == String.class) key = (String) obj;

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