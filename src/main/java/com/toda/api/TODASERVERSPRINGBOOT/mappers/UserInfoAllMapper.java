package com.toda.api.TODASERVERSPRINGBOOT.mappers;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class UserInfoAllMapper implements RowMapper<UserInfoAllDao> {
    private static UserInfoAllMapper mapper = null;
    public static UserInfoAllMapper getInstance(){
        if(mapper == null) mapper = new UserInfoAllMapper();
        return mapper;
    }

    @Override
    public UserInfoAllDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserInfoAllDao.builder()
                .userID(rs.getLong("userID"))
                .userCode(rs.getString("userCode"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .userName(rs.getString("userName"))
                .appPassword(rs.getString("appPassword"))
                .build();
    }
}
