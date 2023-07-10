package com.toda.api.TODASERVERSPRINGBOOT.plugins;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.ValueOperations;

public interface RedisPlugin {
    /**
     * Redis key를 매개변수로 Redis에 저장된 값 get
     * @param key
     * @return
     * @param <T>
     */
    default <T> T getRedis(String key, Class<T> c) throws InvalidProtocolBufferException {
        byte[] bytes = getValueOperations().get(key);
        if(c == UserInfoAllDao.class){
            UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.parseFrom(bytes);
            UserInfoAllDao userInfoAllDao = UserInfoAllDao.builder()
                    .userID(userProto.getUserID())
                    .userCode(userProto.getUserCode())
                    .email(userProto.getEmail())
                    .password(userProto.getPassword())
                    .userName(userProto.getUserName())
                    .appPassword(userProto.getAppPassword())
                    .build();
            @SuppressWarnings ("unchecked") T res = (T) userInfoAllDao;
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
        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
                .setUserID(userInfoAllDao.getUserID())
                .setUserCode(userInfoAllDao.getUserCode())
                .setEmail(userInfoAllDao.getEmail())
                .setPassword(userInfoAllDao.getPassword())
                .setUserName(userInfoAllDao.getUserName())
                .setAppPassword(userInfoAllDao.getAppPassword())
                .build();

        getValueOperations().set(key,userProto.toByteArray());
    }

    default UserInfoAllDao getUserInfo(String email) throws InvalidProtocolBufferException {
        UserInfoAllDao userInfoAllDao = getRedis(email, UserInfoAllDao.class);
        if(userInfoAllDao == null){
            userInfoAllDao = getRepository().getUserInfoAll(email);
            setRedis(email,String.class);
        }
        if(!userInfoAllDao.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        return userInfoAllDao;
    }

    /**
     * redisTemplate.opsForValue() getter
     * @return
     */
    ValueOperations<String, byte[]> getValueOperations();

    /**
     * AuthRepository getter
     * @return
     */
    AuthRepository getRepository();

}