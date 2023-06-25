package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import io.jsonwebtoken.Claims;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public final class UserInfoAllDao extends AbstractModel implements BaseModel {
    private long userID;
    @NonNull String userCode;
    @NonNull String email;
    @NonNull String password;
    @NonNull String userName;
    @NonNull String appPassword;

    public UserInfoAllDao(){}

    @Builder
    public UserInfoAllDao(
            long userID,
            @NonNull String userCode,
            @NonNull String email,
            @NonNull String password,
            @NonNull String userName,
            @NonNull String appPassword
    ){
        this.userID = userID;
        this.userCode = userCode;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.appPassword = appPassword;
    }

    public boolean isSameTokenAttributes(Claims claims){
        return Long.parseLong(String.valueOf(claims.get("userID"))) == userID &&
                ((String) claims.get("userCode")).equals(userCode) &&
                ((String) claims.get("email")).equals(email) &&
                ((String) claims.get("userName")).equals(userName) &&
                ((String) claims.get("appPassword")).equals(appPassword);
    }

    public static UserInfoAllDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        long userID = rs.getLong("userID");
        String userCode = rs.getString("userCode");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String userName = rs.getString("userName");
        String appPassword = rs.getString("appPassword");

        return new UserInfoAllDao(userID, userCode, email, password, userName, appPassword);
    }

    @Override
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
