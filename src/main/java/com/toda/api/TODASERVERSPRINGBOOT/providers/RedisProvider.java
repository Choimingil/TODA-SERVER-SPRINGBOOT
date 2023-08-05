package com.toda.api.TODASERVERSPRINGBOOT.providers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserImage;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.AbstractProvider;
import com.toda.api.TODASERVERSPRINGBOOT.providers.base.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AuthRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserImageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class RedisProvider extends AbstractProvider implements BaseProvider {
    private final AuthRepository authRepository;
    private final UserImageRepository userImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final TokenProvider tokenProvider;

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
     * @param user : 유저 데이터(User type)
     * @param profile : 프로필 url
     */
    @Async
    @Transactional
    private void setRedis(User user, String profile){
        UserInfoProto.UserInfo userProto = UserInfoProto.UserInfo.newBuilder()
                .setUserID(user.getUserID())
                .setUserCode(user.getUserCode())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setUserName(user.getUserName())
                .setAppPassword(String.valueOf(user.getAppPassword()))
                .setCreateAt(user.getCreateAt().toString())
                .setProfile(profile)
                .build();
        redisTemplate.opsForValue().set(user.getEmail(),userProto.toByteArray());
    }

    /**
     * 비동기 Redis 접속해서 key 삭제
     * @param key
     */
    @Async
    @Transactional
    private void deleteRedis(String key){
        redisTemplate.delete(key);
    }

    /**
     * 비밀번호가 해싱되어있지 않은 경우 인코딩 진행
     * @param user
     */
    @Transactional
    private void encodePassword(User user){
        if(user.getPassword().length() < 25) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            authRepository.save(user);
            authRepository.setUserPasswordEncoded(user.getEmail(), encodedPassword);
        }
    }

    /**
     * MDC에 읽은 유저 정보 저장
     * @param user
     * @param profile
     */
    @Transactional
    private void setMdc(User user, String profile){
        MDC.put(TokenFields.USER_ID.value, String.valueOf(user.getUserID()));
        MDC.put(TokenFields.USER_CODE.value, String.valueOf(user.getUserCode()));
        MDC.put(TokenFields.EMAIL.value, String.valueOf(user.getEmail()));
        MDC.put(TokenFields.PASSWORD.value, String.valueOf(user.getPassword()));
        MDC.put(TokenFields.USER_NAME.value, String.valueOf(user.getUserName()));
        MDC.put(TokenFields.APP_PASSWORD.value, String.valueOf(user.getAppPassword()));
        MDC.put(TokenFields.CREATE_AT.value, String.valueOf(user.getCreateAt()));
        MDC.put(TokenFields.PROFILE.value, profile);
    }

    /**
     * MDC에 저장한 유저 정보 삭제
     */
    @Transactional
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

    /**
     * 외부 클래스에서 Redis에 저장된 유저 정보 Get 시 사용
     * @param email : Redis key
     * @return : User type
     */
    public User getUserInfo(String email){
        if(MDC.get(TokenFields.USER_ID.value) == null){
            User user = getRedis(email);
            if(user == null){
                user = authRepository.findByEmail(email);
                UserImage userImage = userImageRepository.findByUserIDAndStatusNot(user.getUserID(),0);
                String url = userImage.getUrl();
                setUserInfo(user,url);
            }
            if(!user.getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
            return user;
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return User.builder()
                    .userID(Long.parseLong(String.valueOf(MDC.get(TokenFields.USER_ID.value))))
                    .userCode(String.valueOf(MDC.get(TokenFields.USER_CODE.value)))
                    .password(MDC.get(TokenFields.PASSWORD.value))
                    .appPassword(Integer.parseInt(String.valueOf(MDC.get(TokenFields.APP_PASSWORD.value))))
                    .email(String.valueOf(MDC.get(TokenFields.EMAIL.value)))
                    .userName(String.valueOf(MDC.get(TokenFields.USER_NAME.value)))
                    .createAt(LocalDateTime.parse(String.valueOf(MDC.get(TokenFields.CREATE_AT.value)), formatter))
                    .build();
        }
    }

    /**
     * 비밀번호 변경, 앱 잠금 진행 시 Redis 정보 리셋
     * @param user
     * @param profile
     */
    public void setUserInfo(User user, String profile){
        if(user.getPassword()==null){
            User origin = getUserInfo(user.getEmail());
            user.setPassword(origin.getPassword());
        }

        setMdc(user, profile);
        setRedis(user, profile);
        encodePassword(user);
    }

    /**
     * Redis에 저장되어 있는 유저 정보 삭제
     * @param user
     */
    public void deleteUserInfo(User user){
        removeMdc();
        // Redis에 존재하는지 확인
        deleteRedis(user.getEmail());
    }
}