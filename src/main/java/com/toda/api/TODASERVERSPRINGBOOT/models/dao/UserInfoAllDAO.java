package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserInfoAllDAO {
    Long userID;
    String userCode;
    String email;
    String password;
    String userName;
    String appPassword;

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
