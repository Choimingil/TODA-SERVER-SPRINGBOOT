package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.UserInfoAllDao;
import com.toda.api.TODASERVERSPRINGBOOT.mappers.UserInfoAllMapper;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.base.AbstractRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthRepository extends AbstractRepository implements BaseRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserInfoAllDao getUserInfoAll(String email) {
        List<String> params = new ArrayList<>(List.of(email));
        return selectOneTuple(
                "SELECT ID as userID, code as userCode, email, password, name as userName, status as appPassword FROM User WHERE email like ?;",
                UserInfoAllMapper.getInstance(),
                params
        );
    }
    public String setUserPasswordEncoded(String email, String encodedPassword) {
        List<String> params = new ArrayList<>(List.of(encodedPassword,email));
        update("UPDATE User SET password = ? where email = ?;",params);
        return "성공";
    }
    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}