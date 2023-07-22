package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.models.mappings.UserInfoMappings;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class RedisProvider extends AbstractProvider implements BaseProvider {
    private final AuthRepository authRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private static UserInfoAllDao userInfo = null;

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * Redis key를 매개변수로 Redis에 저장된 값 get
     * @param key
     * @return
     */
    private UserInfoAllDao getRedis(String key) {
        try {
            UserInfoAllDao userInfoAllDao = getRedisFuture(key).get();
            if(userInfoAllDao.getUserID() == 0) return null;
            else return getRedisFuture(key).get();
        }
        catch (ExecutionException | InterruptedException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    @Async
    private Future<UserInfoAllDao> getRedisFuture(String key) {
        try {
            byte[] bytes = redisTemplate.opsForValue().get(key);
            if(bytes == null)
                return CompletableFuture.completedFuture(UserInfoAllDao.builder()
                        .userID(0)
                        .userCode("")
                        .email("")
                        .password("")
                        .userName("")
                        .appPassword("")
                        .build());

            UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.parseFrom(bytes);
            UserInfoAllDao userInfoAllDao = UserInfoAllDao.builder()
                    .userID(userProto.getUserID())
                    .userCode(userProto.getUserCode())
                    .email(userProto.getEmail())
                    .password(userProto.getPassword())
                    .userName(userProto.getUserName())
                    .appPassword(userProto.getAppPassword())
                    .build();
            return CompletableFuture.completedFuture(userInfoAllDao);
        }
        catch (InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     *
     * class.isInstance : 이 클래스가 특정 클래스 또는 그 클래스의 하위 클래스인지 여부를 확인할 때 사용
     * 장점 : 비교 클래스가 클래스 리터럴 상태가 아니어도 비교 가능
     * 단점 : 정적으로 컴파일 시간에 알려진 클래스일 경우 클래스 리터럴을 이용해서 비교 불가
     * == : 두 클래스가 같은지 확인
     * 장점 : 클래스 리터럴을 이용해서 타입 비교 가능
     * 단점 : 클래스 리터럴 상태가 아닐 경우 비교 불가
     * @param key
     */
    @Async
    private void setRedis(String key, UserInfoAllDao userInfoAllDao){
        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
                .setUserID(userInfoAllDao.getUserID())
                .setUserCode(userInfoAllDao.getUserCode())
                .setEmail(userInfoAllDao.getEmail())
                .setPassword(userInfoAllDao.getPassword())
                .setUserName(userInfoAllDao.getUserName())
                .setAppPassword(userInfoAllDao.getAppPassword())
                .build();
        redisTemplate.opsForValue().set(key,userProto.toByteArray());
    }

//    public void publishMessage(String key) {
//        UserInfoAllDao userInfoAllDao = authRepository.getUserInfoAll(key);
//        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
//                .setUserID(userInfoAllDao.getUserID())
//                .setUserCode(userInfoAllDao.getUserCode())
//                .setEmail(userInfoAllDao.getEmail())
//                .setPassword(userInfoAllDao.getPassword())
//                .setUserName(userInfoAllDao.getUserName())
//                .setAppPassword(userInfoAllDao.getAppPassword())
//                .build();
//
//        byte[] serializedMessage = userProto.toByteArray();
//        redisTemplate.convertAndSend(channelTopic.getTopic(), serializedMessage);
//    }

    // 비밀번호 변경 기능 개발 시 resetRedis 추가


    public UserInfoAllDao getUserInfo(String email){
        if(userInfo == null){
            UserInfoAllDao userInfoAllDao = getRedis(email);
            if(userInfoAllDao == null){
                UserInfoMappings mappings = authRepository.findByEmail(email);
                userInfoAllDao = UserInfoAllDao.builder()
                        .userID(mappings.getUserID())
                        .userCode(mappings.getUserCode())
                        .email(mappings.getEmail())
                        .password(mappings.getPassword())
                        .userName(mappings.getUserName())
                        .appPassword(mappings.getAppPassword())
                        .build();
                setRedis(email, userInfoAllDao);
            }
            if(!userInfoAllDao.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
            userInfo = userInfoAllDao;
        }
        return userInfo;
    }


}