package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.google.protobuf.GeneratedMessageV3;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractRedis {
    /**
     * Redis에서 가져온 프로토콜 버퍼 값을 원하는 객체로 변환하는 제네릭 메서드
     * T : 변환되는 프로토콜 버퍼
     * U : 변환하고자 하는 객체
     * @param key
     * @param protoClass
     * @param converter
     * @param redisTemplate
     * @return
     * @param <T>
     * @param <U>
     */
    public <T extends GeneratedMessageV3, U> U convertRedisData(String key, Class<T> protoClass, Function<T, U> converter, RedisTemplate<String, byte[]> redisTemplate) {
        try {
            byte[] byteCode = getRedis(key,redisTemplate).get();
            if (byteCode == null) return null;
            return converter.apply(parseProto(byteCode, protoClass));
        } catch (ExecutionException | InterruptedException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 Redis 접속해서 데이터 추가
     * @param key
     * @param byteCode
     * @param redisTemplate
     */
//    @Async
    public void setRedis(String key, byte[] byteCode, RedisTemplate<String, byte[]> redisTemplate) {
        redisTemplate.opsForValue().set(key,byteCode);
    }

    /**
     * 비동기 Redis 접속해서 데이터 삭제
     * @param key
     * @param redisTemplate
     */
//    @Async
    public void deleteRedis(String key, RedisTemplate<String, byte[]> redisTemplate) {
        redisTemplate.delete(key);
    }

    /**
     * T 제네릭 클래스를 프로토콜 버퍼의 parseFrom 메서드 실행
     * 주의 : T가 GeneratedMessageV3 내의 parseFrom 생성 가능한 클래스여야 함
     * @param byteCode
     * @param protoClass
     * @return
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private <T extends GeneratedMessageV3> T parseProto(byte[] byteCode, Class<T> protoClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (T) protoClass.getMethod("parseFrom", byte[].class).invoke(null, byteCode);
    }

    /**
     * 비동기 처리로 Redis 접속
     * @param key
     * @param redisTemplate
     * @return : Future<byte[]>
     */
//    @Async
    private Future<byte[]> getRedis(String key, RedisTemplate<String, byte[]> redisTemplate) {
        byte[] bytes = redisTemplate.opsForValue().get(key);
        if(bytes == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(bytes);
    }

    /**
     * 비동기 처리로 Redis 접속해서 여러 키 동시 조회
     * @param keys
     * @param redisTemplate
     * @return : Future<List<byte[]>>
     */
//    @Async
    private Future<List<byte[]>> getRedisList(List<String> keys, RedisTemplate<String, byte[]> redisTemplate) {
        List<byte[]> byteList = redisTemplate.opsForValue().multiGet(keys);
        if(byteList == null) return CompletableFuture.completedFuture(null);
        List<byte[]> res = byteList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return CompletableFuture.completedFuture(res);
    }
}
