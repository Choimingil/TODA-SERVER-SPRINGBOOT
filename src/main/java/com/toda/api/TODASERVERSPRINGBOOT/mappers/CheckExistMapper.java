package com.toda.api.TODASERVERSPRINGBOOT.mappers;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.CheckExistDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class CheckExistMapper implements RowMapper<CheckExistDao> {
    private static CheckExistMapper mapper = null;
    public static CheckExistMapper getInstance(){
        if(mapper == null) mapper = new CheckExistMapper();
        return mapper;
    }
    @Override
    public CheckExistDao mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CheckExistDao.builder()
                .exist(rs.getInt("exist"))
                .build();
    }
}
