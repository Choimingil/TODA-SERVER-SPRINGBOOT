package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import com.toda.api.TODASERVERSPRINGBOOT.models.dto.requests.ValidateEmailDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SystemRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean isExistEmail(String email) {
        List<Boolean> results = jdbcTemplate.query(
                "SELECT EXISTS(SELECT * FROM User WHERE email= ? and status not like 99999) AS exist;",
                new RowMapper<Boolean>() {
                    @Override
                    public Boolean mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt("exist") == 1;
                    }
                }, email);
        return results.get(0);
    }
}
