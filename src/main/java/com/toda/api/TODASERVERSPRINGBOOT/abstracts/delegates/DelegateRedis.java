package com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates;

import com.google.protobuf.GeneratedMessageV3;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseRedis;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class DelegateRedis implements BaseRedis {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public <T extends GeneratedMessageV3, U> U convertRedisData(String key, Class<T> protoClass, Function<T, U> converter) {
        try {
            byte[] byteCode = getRedis(key).get();
            if (byteCode == null) return null;
            return converter.apply(parseProto(byteCode, protoClass));
        } catch (ExecutionException | InterruptedException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
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
     */
    private <T extends GeneratedMessageV3> T parseProto(byte[] byteCode, Class<T> protoClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (T) protoClass.getMethod("parseFrom", byte[].class).invoke(null, byteCode);
    }

    /**
     * 비동기 처리로 Redis 접속
     * @param key
     * @return : Future<byte[]>
     */
    private Future<byte[]> getRedis(String key) {
        return CompletableFuture.supplyAsync(() -> redisTemplate.opsForValue().get(key), taskExecutor);
    }

    /**
     * 비동기 처리로 Redis 접속해서 여러 키 동시 조회
     * @param keys
     * @return : Future<List<byte[]>>
     */
    private Future<List<byte[]>> getRedisList(List<String> keys) {
        return CompletableFuture.supplyAsync(() -> {
            List<byte[]> byteList = redisTemplate.opsForValue().multiGet(keys);
            if (byteList == null) return null;
            return byteList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        });
    }


    /**
     * 비동기 처리로 Redis 데이터 저장
     * @param key
     * @param byteCode
     * @return
     */
    @Override
    public Void setRedis(String key, byte[] byteCode) {
        try {
            return CompletableFuture.runAsync(() -> redisTemplate.opsForValue().set(key,byteCode), taskExecutor).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    /**
     * 비동기 처리로 Redis 데이터 삭제
     * @param key
     * @return
     */
    @Override
    public Void deleteRedis(String key) {
        try {
            return CompletableFuture.runAsync(() -> redisTemplate.delete(key), taskExecutor).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }
}
