package com.toda.api.TODASERVERSPRINGBOOT.models.dtos;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
public final class UserData {
    private long userID;
    private String email;
    private String password;
    private String userCode;
    private int appPassword;
    private String userName;
    private LocalDateTime createAt;
    private String profile;

    public User toUser(){
        return User.builder()
                .userID(userID)
                .userCode(userCode)
                .userName(userName)
                .email(email)
                .password(password)
                .appPassword(appPassword)
                .createAt(createAt)
                .build();
    }

    public UserData(){}

    @Builder
    public UserData(
            long userID,
            String email,
            String password,
            String userCode,
            int appPassword,
            String userName,
            LocalDateTime createAt,
            String profile
    ){
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.userCode = userCode;
        this.appPassword = appPassword;
        this.userName = userName;
        this.createAt = createAt;
        this.profile = profile;
    }
}
