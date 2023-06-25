package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.dao.CheckExistDao;
import com.toda.api.TODASERVERSPRINGBOOT.mappers.CheckExistMapper;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.base.AbstractRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.base.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SystemRepository extends AbstractRepository implements BaseRepository{
    private final JdbcTemplate jdbcTemplate;

    public boolean isExistEmail(String email) {
        List<String> params = new ArrayList<>(List.of(email));
        CheckExistDao res = selectOneTuple(
                "SELECT EXISTS(SELECT * FROM User WHERE email= ? and status not like 99999) as exist;",
                CheckExistMapper.getInstance(),
                params
        );
        logger.info(String.valueOf(res.getExist()));
        return res.getExist()==1;
    }


    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
