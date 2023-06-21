package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserInfoAllDAO getUserInfoAll(String email) {
        Object[] res = {email};
        return (UserInfoAllDAO) jdbcTemplate.queryForObject(
                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;"
                ,new BeanPropertyRowMapper<>(UserInfoAllDAO.class)
                ,res
        );

//        Object[] res = {email};
//        List<UserInfoAllDAO> results = jdbcTemplate.query(
//                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;"
//                ,new BeanPropertyRowMapper<>(UserInfoAllDAO.class)
//                ,res
//        );
//        return results;
    }

    public String setUserPasswordEncoded(String email, String encodedPassword) {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE User SET password = ? where email = ?;");

        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query.toString());
            prepareStatement.setString(1, encodedPassword);
            prepareStatement.setString(2, email);
            return prepareStatement;
        };
        jdbcTemplate.update(preparedStatementCreator);
        return "성공";
    }

}
