package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractAuth;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseUserAuth;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserInfoDetail;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
import com.toda.api.TODASERVERSPRINGBOOT.models.dtos.UserData;
import com.toda.api.TODASERVERSPRINGBOOT.models.protobuffers.UserInfoProto;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public final class DelegateUserAuth extends AbstractAuth implements BaseUserAuth {
    private final DelegateRedis delegateRedis;
    private final UserRepository userRepository;

    public DelegateUserAuth(
            DelegateJwt delegateJwt,
            DelegateMdc delegateMdc,
            DelegateRedis delegateRedis,
            UserRepository userRepository
    ) {
        super(delegateJwt, delegateMdc);
        this.delegateRedis = delegateRedis;
        this.userRepository = userRepository;
    }

    @Override
    public UserData getUserInfo(String value) {
        String email = value.length()>45 ? decodeToken(value).getEmail() : value;

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

    @Override
    public void setUserInfo(UserData userData) {
        setMdc(userData);
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
        delegateRedis.setRedis(userData.getEmail(), userProto.toByteArray());
    }

    @Override
    public void deleteUserInfo(String email) {
        removeMdc();
        delegateRedis.deleteRedis(email);
    }

    /**
     * Redis 접속해서 key(Email)값으로 데이터 조회
     * @param key : email
     * @return : UserData type
     */
    private UserData getUserData(String key) {
        return delegateRedis.convertRedisData(key, UserInfoProto.UserInfo.class, userProto -> UserData.builder()
                .userID(userProto.getUserID())
                .userCode(userProto.getUserCode())
                .email(userProto.getEmail())
                .password(userProto.getPassword())
                .userName(userProto.getUserName())
                .appPassword(Integer.parseInt(userProto.getAppPassword()))
                .createAt(LocalDateTime.parse(userProto.getCreateAt()))
                .profile(userProto.getProfile())
                .build());
    }
}
