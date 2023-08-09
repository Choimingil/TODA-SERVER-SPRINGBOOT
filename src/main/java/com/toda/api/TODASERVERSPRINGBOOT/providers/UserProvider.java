package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserImageRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class UserProvider extends AbstractProvider implements BaseProvider {
    private final UserRepository userRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final TokenProvider tokenProvider;


    /**
     * 외부 클래스에서 Redis에 저장된 유저 정보 Get 시 사용
     * @param value : email or token (문자열 길이로 구별)
     * @return : User type
     */
    public UserData getUserInfo(String value){
        String email = value.length()>45 ? tokenProvider.decodeToken(value).getEmail() : value;

        if(MDC.get(TokenFields.USER_ID.value) == null){
            UserData userData = getUserData(email);
            if(userData == null){
                UserInfoDetail user = userRepository.getUserDataByEmail(email);
                userData = UserData.builder()
                        .userID(user.getUserID())
                        .userCode(user.getUserCode())
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .appPassword(user.getAppPassword())
                        .createAt(user.getCreateAt())
                        .profile(user.getProfile())
                        .build();
                setUserInfo(userData);
            }
            if(!userData.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
            return userData;
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return UserData.builder()
                    .userID(Long.parseLong(String.valueOf(MDC.get(TokenFields.USER_ID.value))))
                    .userCode(String.valueOf(MDC.get(TokenFields.USER_CODE.value)))
                    .password(MDC.get(TokenFields.PASSWORD.value))
                    .appPassword(Integer.parseInt(String.valueOf(MDC.get(TokenFields.APP_PASSWORD.value))))
                    .email(String.valueOf(MDC.get(TokenFields.EMAIL.value)))
                    .userName(String.valueOf(MDC.get(TokenFields.USER_NAME.value)))
                    .createAt(LocalDateTime.parse(String.valueOf(MDC.get(TokenFields.CREATE_AT.value)), formatter))
                    .profile(String.valueOf(MDC.get(TokenFields.PROFILE.value)))
                    .build();
        }
    }

    /**
     * MDC 및 Redis 정보 추가
     * @param userData
     */
    public void setUserInfo(UserData userData){
        setMdc(userData);

        try {
            setRedis(userData).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * MDC 및 Redis에 저장되어 있는 유저 정보 삭제
     * @param email
     */
    public void deleteUserInfo(String email){
        removeMdc();

        try {
            deleteRedis(email).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * Redis 접속해서 key(Email)값으로 데이터 조회
     * @param key : email
     * @return : UserData type
     */
    private UserData getUserData(String key) {
        try {
            UserInfoProto.UserInfo userProto = getRedis(key).get();
            if(userProto == null) return null;
            return UserData.builder()
                    .userID(userProto.getUserID())
                    .userCode(userProto.getUserCode())
                    .email(userProto.getEmail())
                    .password(userProto.getPassword())
                    .userName(userProto.getUserName())
                    .appPassword(Integer.parseInt(userProto.getAppPassword()))
                    .createAt(LocalDateTime.parse(userProto.getCreateAt()))
                    .profile(userProto.getProfile())
                    .build();
        }
        catch (ExecutionException | InterruptedException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 처리로 Redis 접속
     * @param key : email
     * @return : Future<UserData>
     */
    @Async
    private Future<UserInfoProto.UserInfo> getRedis(String key) {
        try {
            byte[] bytes = redisTemplate.opsForValue().get(key);
            if(bytes == null)
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(UserInfoProto.UserInfo.parseFrom(bytes));
        }
        catch (InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 Redis 접속해서 데이터 추가
     * @param userData
     */
    @Async
    private Future<Void> setRedis(UserData userData){
        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
                .setUserID(userData.getUserID())
                .setUserCode(userData.getUserCode())
                .setEmail(userData.getEmail())
                .setPassword(userData.getPassword())
                .setUserName(userData.getUserName())
                .setAppPassword(String.valueOf(userData.getAppPassword()))
                .setCreateAt(userData.getCreateAt().toString())
                .setProfile(userData.getProfile())
                .build();
        redisTemplate.opsForValue().set(userData.getEmail(),userProto.toByteArray());
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 비동기 Redis 접속해서 데이터 삭제
     * @param key
     * @return
     */
    @Async
    private Future<Void> deleteRedis(String key){
        redisTemplate.delete(key);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * MDC에 읽은 유저 정보 저장
     * @param userData
     */
    private void setMdc(UserData userData){
        MDC.put(TokenFields.USER_ID.value, String.valueOf(userData.getUserID()));
        MDC.put(TokenFields.USER_CODE.value, userData.getUserCode());
        MDC.put(TokenFields.EMAIL.value, userData.getEmail());
        MDC.put(TokenFields.PASSWORD.value, userData.getPassword());
        MDC.put(TokenFields.USER_NAME.value, userData.getUserName());
        MDC.put(TokenFields.APP_PASSWORD.value, String.valueOf(userData.getAppPassword()));
        MDC.put(TokenFields.CREATE_AT.value, userData.getCreateAt().toString());
        MDC.put(TokenFields.PROFILE.value, userData.getProfile());
    }

    /**
     * MDC에 저장한 유저 정보 삭제
     */
    private void removeMdc(){
        MDC.remove(TokenFields.USER_ID.value);
        MDC.remove(TokenFields.USER_CODE.value);
        MDC.remove(TokenFields.EMAIL.value);
        MDC.remove(TokenFields.PASSWORD.value);
        MDC.remove(TokenFields.USER_NAME.value);
        MDC.remove(TokenFields.APP_PASSWORD.value);
        MDC.remove(TokenFields.CREATE_AT.value);
        MDC.remove(TokenFields.PROFILE.value);
    }
}