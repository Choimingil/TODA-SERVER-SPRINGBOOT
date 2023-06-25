package com.toda.api.TODASERVERSPRINGBOOT.repositories.base;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

public abstract class AbstractRepository implements BaseRepository {
    protected final Logger logger = LoggerFactory.getLogger(AbstractRepository.class);

    /**
     * SELECT 여러 tuple의 query 실행 메소드 구현
     * @param query
     * @param mapper
     * @param params
     * @return
     * @param <T>
     */
    @Override
    public <T> List<T> selectList(String query, RowMapper<T> mapper, List<String> params){
        return getJdbcTemplate().query(
                query,
                mapper,
                params.toArray()
        );
    }

    /**
     * SELECT 1개의 tuple의 query 실행 메소드 구현
     * @param query
     * @param mapper
     * @param params
     * @return
     * @param <T>
     */
    @Override
    public <T> T selectOneTuple(String query, RowMapper<T> mapper, List<String> params){
        return getJdbcTemplate().queryForObject(
                query,
                mapper,
                params.toArray()
        );
    }

    /**
     * UPDATE query 실행 메소드 구현
     * @param query
     * @param params
     */
    @Override
    public void update(String query, List<String> params){
        PreparedStatementCreator preparedStatementCreator = (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            for(int idx=1;idx<=params.size();idx++) prepareStatement.setString(idx, params.get(idx-1));
            return prepareStatement;
        };
        getJdbcTemplate().update(preparedStatementCreator);
    }
}
