package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.UserInfo;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class RedisProvider extends AbstractProvider implements BaseProvider {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, byte[]> redisTemplate;

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * Redis 접속해서 key(Email)값으로 데이터 조회
     * @param key : email
     * @return : User type
     */
    private User getRedis(String key) {
        try {
            User user = getRedisFuture(key).get();
            if(user.getUserID() == 0) return null;
            else return getRedisFuture(key).get();
        }
        catch (ExecutionException | InterruptedException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 처리로 Redis 접속
     * @param key : email
     * @return : Future<User>
     */
    @Async
    private Future<User> getRedisFuture(String key) {
        try {
            byte[] bytes = redisTemplate.opsForValue().get(key);
            if(bytes == null)
                return CompletableFuture.completedFuture(User.builder()
                        .userID(0)
                        .userCode("")
                        .email("")
                        .password("")
                        .userName("")
                        .appPassword(99999)
                        .build());

            UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.parseFrom(bytes);
            User user = User.builder()
                    .userID(userProto.getUserID())
                    .userCode(userProto.getUserCode())
                    .email(userProto.getEmail())
                    .password(userProto.getPassword())
                    .userName(userProto.getUserName())
                    .appPassword(Integer.parseInt(userProto.getAppPassword()))
                    .build();
            return CompletableFuture.completedFuture(user);
        }
        catch (InvalidProtocolBufferException e){
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 Redis 접속해서 데이터 추가
     * @param key : email
     * @param user : 유저 데이터(User type)
     */
    @Async
    private void setRedis(String key, User user){
        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
                .setUserID(user.getUserID())
                .setUserCode(user.getUserCode())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setUserName(user.getUserName())
                .setAppPassword(String.valueOf(user.getAppPassword()))
                .build();
        redisTemplate.opsForValue().set(key,userProto.toByteArray());
    }

    /**
     * 비밀번호가 해싱되어있지 않은 경우 인코딩 진행
     * @param user
     */
    private void encodePassword(User user){
        if(user.getPassword().length() < 25) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            authRepository.save(user);
            authRepository.setUserPasswordEncoded(user.getEmail(), encodedPassword);
        }
    }

    private void setMdc(User user){
        MDC.put("userID", String.valueOf(user.getUserID()));
        MDC.put("userCode", String.valueOf(user.getUserCode()));
        MDC.put("email", String.valueOf(user.getEmail()));
        MDC.put("password", String.valueOf(user.getPassword()));
        MDC.put("userName", String.valueOf(user.getUserName()));
        MDC.put("appPassword", String.valueOf(user.getAppPassword()));
    }

    /**
     * 외부 클래스에서 Redis에 저장된 유저 정보 Get 시 사용
     * @param email : Redis key
     * @return : User type
     */
    public User getUserInfo(String email){
        if(MDC.get("userID") == null){
            User user = getRedis(email);
            if(user == null){
                UserInfo mappings = authRepository.findByEmail(email);
                user = User.builder()
                        .userID(mappings.getUserID())
                        .userCode(mappings.getUserCode())
                        .email(mappings.getEmail())
                        .password(mappings.getPassword())
                        .userName(mappings.getUserName())
                        .appPassword(Integer.parseInt(mappings.getAppPassword()))
                        .build();

                setMdc(user);
                setRedis(email, user);
                encodePassword(user);
            }
            if(!user.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
            return user;
        }
        else{
            return User.builder()
                    .userID(Long.parseLong(MDC.get("userID")))
                    .userCode(MDC.get("userCode"))
                    .email(MDC.get("email"))
                    .password(MDC.get("password"))
                    .userName(MDC.get("userName"))
                    .appPassword(Integer.parseInt(MDC.get("appPassword")))
                    .build();
        }
    }

    /**
     * 비밀번호 변경, 앱 잠금 진행 시 Redis 정보 리셋
     * @param user
     */
    public void resetUserInfo(User user){
        setMdc(user);
        setRedis(user.getEmail(), user);
        encodePassword(user);
    }
}