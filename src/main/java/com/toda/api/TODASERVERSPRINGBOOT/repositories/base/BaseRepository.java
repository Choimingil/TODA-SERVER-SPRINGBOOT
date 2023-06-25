package com.toda.api.TODASERVERSPRINGBOOT.repositories.base;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public interface BaseRepository {
    /**
     * SELECT 여러 tuple의 query 실행 메소드
     * @param query
     * @param mapper
     * @param params
     * @return
     * @param <T>
     */
    <T> List<T> selectList(String query, RowMapper<T> mapper, List<String> params);

    /**
     * SELECT 1개의 tuple의 query 실행 메소드
     * @param query
     * @param mapper
     * @param params
     * @return
     * @param <T>
     */
    <T> T selectOneTuple(String query, RowMapper<T> mapper, List<String> params);

    /**
     * UPDATE query 실행 메소드
     * @param query
     * @param params
     */
    void update(String query, List<String> params);

    /**
     * JdbcTemplate getter
     * @return
     */
    JdbcTemplate getJdbcTemplate();
}
