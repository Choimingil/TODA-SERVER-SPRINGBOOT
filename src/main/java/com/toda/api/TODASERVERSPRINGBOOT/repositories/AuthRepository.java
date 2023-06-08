package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDAO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserInfoAllDAO getUserInfoAll(String email) {
        List<UserInfoAllDAO> results = jdbcTemplate.query(
                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email = ?;",
                new RowMapper<UserInfoAllDAO>() {
                    @Override
                    public UserInfoAllDAO mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
                        UserInfoAllDAO userInfoAllDAO = new UserInfoAllDAO();

                        userInfoAllDAO.setUserID(rs.getLong("userID"));
                        userInfoAllDAO.setUserCode(rs.getString("userCode"));
                        userInfoAllDAO.setEmail(rs.getString("email"));
                        userInfoAllDAO.setPassword(rs.getString("password"));
                        userInfoAllDAO.setUserName(rs.getString("username"));
                        userInfoAllDAO.setAppPassword(rs.getString("appPassword"));

                        return userInfoAllDAO;
                    }
                }, email);
        return results.get(0);
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
