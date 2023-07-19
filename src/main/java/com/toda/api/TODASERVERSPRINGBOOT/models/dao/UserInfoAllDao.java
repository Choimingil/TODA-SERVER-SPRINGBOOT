package com.toda.api.TODASERVERSPRINGBOOT.models.dao;

import com.toda.api.TODASERVERSPRINGBOOT.models.base.AbstractModel;
import com.toda.api.TODASERVERSPRINGBOOT.models.base.BaseModel;
import io.jsonwebtoken.Claims;
import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("userID : ");
        sb.append(userID);
        sb.append(", ");
        sb.append("userCode : ");
        sb.append(userCode);
        sb.append(", ");
        sb.append("email : ");
        sb.append(email);
        sb.append(", ");
        sb.append("password : ");
        sb.append(password);
        sb.append(", ");
        sb.append("userName : ");
        sb.append(userName);
        sb.append(", ");
        sb.append("appPassword : ");
        sb.append(appPassword);
        return sb.toString();
    }
}
