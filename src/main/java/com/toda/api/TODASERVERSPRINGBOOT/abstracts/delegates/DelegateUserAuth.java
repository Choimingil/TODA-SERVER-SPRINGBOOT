package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractAuth;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseUserAuth;
import com.toda.api.TODASERVERSPRINGBOOT.entities.User;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.UserDetail;
import com.toda.api.TODASERVERSPRINGBOOT.enums.TokenFields;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.BusinessLogicException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.NoArgException;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongArgException;
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
    public UserDetail getUserInfo(String value) {
        String email = value.length()>45 ? getEmailWithDecodeToken(value) : value;

        UserDetail userDetail = getUserDetailOnCache(email);
        if(userDetail == null){
            userDetail = userRepository.getUserDetailByEmail(email);
            if(userDetail == null) throw new NoArgException(NoArgException.of.NO_EMAIL_EXCEPTION);
            updateUserRedis(userDetail.getUser(), userDetail.getProfile());
        }
        if(!userDetail.getUser().getEmail().equals(email)) throw new WrongArgException(WrongArgException.of.WRONG_BODY_EXCEPTION);
        return userDetail;
    }

    @Override
    public void updateUserRedis(User user, String profile) {
        setMdc(user,profile);
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
        delegateRedis.setRedis(user.getEmail(), userProto.toByteArray());
    }

    @Override
    public void deleteUserInfo(String email) {
        removeMdc();
        delegateRedis.deleteRedis(email);
    }

    /**
     * Redis 접속해서 key(Email)값으로 데이터 조회
     * @param key : email
     * @return : UserDetail type
     */
    private UserDetail getUserDetailOnCache(String key) {
        return delegateRedis.convertRedisData(key, UserInfoProto.UserInfo.class, userProto -> new UserDetail() {
                    @Override
                    public User getUser() {
                        return new User(
                                userProto.getUserID(),
                                userProto.getEmail(),
                                userProto.getPassword(),
                                userProto.getUserCode(),
                                Integer.parseInt(userProto.getAppPassword()),
                                userProto.getUserName(),
                                LocalDateTime.parse(userProto.getCreateAt())
                        );
                    }

                    @Override
                    public String getProfile() {
                        return userProto.getProfile();
                    }
                }
        );
    }
}
