package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class UserInfoAllDAO {
    @NotNull
    long userID;
    @NotNull
    String userCode;
    @NotNull
    String email;
    @NotNull
    String password;
    @NotNull
    String userName;
    @NotNull
    String appPassword;

    public UserInfoAllDAO(){}

    @Builder
    public UserInfoAllDAO(
            long userID,
            String userCode,
            String email,
            String password,
            String userName,
            String appPassword
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
}
