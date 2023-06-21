package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import com.toda.api.TODASERVERSPRINGBOOT.utils.exceptions.ValidationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SystemRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean isExistEmail(String email) {
        Object[] res = {email};
        int result = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT * FROM User WHERE email= ? and status not like 99999) AS exist;"
                ,Integer.class
                ,res
        );

        return result == 1;
    }


}
