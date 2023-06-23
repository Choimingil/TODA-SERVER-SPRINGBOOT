package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UserInfoAllDAO {
    long userID;
    @NotNull String userCode;
    @NotNull String email;
    @NotNull String password;
    @NotNull String userName;
    @NotNull String appPassword;

    public UserInfoAllDAO(){}

    @Builder
    public UserInfoAllDAO(
            long userID,
            @NotNull String userCode,
            @NotNull String email,
            @NotNull String password,
            @NotNull String userName,
            @NotNull String appPassword
    ){
        this.userID = userID;
        this.userCode = userCode;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.appPassword = appPassword;
    }

    public String toString(){
        return "userID : " +
                userID +
                ", " +
                "userCode : " +
                userCode +
                ", " +
                "email : " +
                email +
                ", " +
                "password : " +
                password +
                ", " +
                "userName : " +
                userName +
                ", " +
                "appPassword : " +
                appPassword;
    }

    public boolean isSameTokenAttributes(Claims claims){
        return Long.parseLong(String.valueOf(claims.get("userID"))) == userID &&
                ((String) claims.get("userCode")).equals(userCode) &&
                ((String) claims.get("email")).equals(email) &&
                ((String) claims.get("userName")).equals(userName) &&
                ((String) claims.get("appPassword")).equals(appPassword);
    }
}
