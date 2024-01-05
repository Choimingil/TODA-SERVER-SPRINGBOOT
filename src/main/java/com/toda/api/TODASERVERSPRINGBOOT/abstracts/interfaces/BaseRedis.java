package com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces;

import com.google.protobuf.GeneratedMessageV3;

import java.util.function.Function;

public interface BaseRedis {
    /**
     * Redis에서 가져온 프로토콜 버퍼 값을 원하는 객체로 변환하는 제네릭 메서드
     * T : 변환되는 프로토콜 버퍼
     * U : 변환하고자 하는 객체
     * @param key
     * @param protoClass
     * @param converter
     * @return
     * @param <T>
     * @param <U>
     */
    <T extends GeneratedMessageV3, U> U convertRedisData(String key, Class<T> protoClass, Function<T, U> converter);

    /**
     * 비동기 처리로 Redis 데이터 저장
     * @param key
     * @param byteCode
     * @return
     */
    Void setRedis(String key, byte[] byteCode);

    /**
     * 비동기 처리로 Redis 데이터 삭제
     * @param key
     * @return
     */
    Void deleteRedis(String key);
}
