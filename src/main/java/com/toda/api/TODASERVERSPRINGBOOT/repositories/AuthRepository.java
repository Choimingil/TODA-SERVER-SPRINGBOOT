package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserInfoAllDAO getUserInfoAll(String email) {
        Object[] params = {email};
        // 이 방식의 경우 코드가 간결해지는 장점이 있지만 mapper에 setter가 필요해 final class 생성 불가
//        return (UserInfoAllDAO) jdbcTemplate.queryForObject(
//                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;"
//                ,new BeanPropertyRowMapper<>(UserInfoAllDAO.class)
//                ,params
//        );

        return (UserInfoAllDAO) jdbcTemplate.queryForObject(
                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;",
                (rs, rowNum) -> {
                    return UserInfoAllDAO.builder()
                            .userID(rs.getLong("userID"))
                            .userCode(rs.getString("userCode"))
                            .email(rs.getString("email"))
                            .password(rs.getString("password"))
                            .userName(rs.getString("userName"))
                            .appPassword(rs.getString("appPassword"))
                            .build();
                },
                params
        );

        // 여기는 List 쿼리 가져올 때 사용

//        Object[] res = {email};
//        List<UserInfoAllDAO> results = jdbcTemplate.query(
//                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;"
//                ,new BeanPropertyRowMapper<>(UserInfoAllDAO.class)
//                ,res
//        );
//        return results;

//        List<UserInfoAllDAO> res = jdbcTemplate.query(
//                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;",
//                (rs, rowNum) -> {
//                    return UserInfoAllDAO.builder()
//                            .userID(rs.getLong("userID"))
//                            .userCode(rs.getString("userCode"))
//                            .email(rs.getString("email"))
//                            .password(rs.getString("password"))
//                            .userName(rs.getString("userName"))
//                            .appPassword(rs.getString("appPassword"))
//                            .build();
//                },
//                params
//        );
//        return res.get(0);
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
