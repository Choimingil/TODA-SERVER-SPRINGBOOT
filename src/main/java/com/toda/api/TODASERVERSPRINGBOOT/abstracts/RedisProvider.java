package com.toda.api.TODASERVERSPRINGBOOT.abstracts;

import com.google.protobuf.InvalidProtocolBufferException;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseProvider;
import com.toda.api.TODASERVERSPRINGBOOT.exceptions.WrongAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.protobuf.GeneratedMessageV3;

@RequiredArgsConstructor
public abstract class RedisProvider extends AbstractProvider implements BaseProvider {
    protected <T extends GeneratedMessageV3, U> U convertRedisData(String key, Class<T> protoClass, Function<T, U> converter) {
        try {
            byte[] byteCode = getRedis(key).get();
            if (byteCode == null) return null;
            return converter.apply(parseProto(byteCode, protoClass));
        } catch (ExecutionException | InterruptedException | NoSuchMethodException |
                 InvocationTargetException | IllegalAccessException e) {
            throw new WrongAccessException(WrongAccessException.of.REDIS_CONNECTION_EXCEPTION);
        }
    }

    private <T extends GeneratedMessageV3> T parseProto(byte[] byteCode, Class<T> protoClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (T) protoClass.getMethod("parseFrom", byte[].class).invoke(null, byteCode);
    }






    /**
     * 비동기 처리로 Redis 접속
     * @param key
     * @return : Future<byte[]>
     */
    @Async
    private Future<byte[]> getRedis(String key) {
        byte[] bytes = getRedisTemplate().opsForValue().get(key);
        if(bytes == null)
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.completedFuture(bytes);
    }

    /**
     * 비동기 처리로 Redis 접속해서 여러 키 동시 조회
     * @param keys
     * @return : Future<List<byte[]>>
     */
    @Async
    private Future<List<byte[]>> getRedisList(List<String> keys) {
        List<byte[]> byteList = getRedisTemplate().opsForValue().multiGet(keys);
        if(byteList == null) return CompletableFuture.completedFuture(null);

        List<byte[]> res = byteList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(res);
    }

    /**
     * 비동기 Redis 접속해서 데이터 추가
     * @param key
     * @param byteCode
     */
    @Async
    protected void setRedis(String key, byte[] byteCode){
        getRedisTemplate().opsForValue().set(key,byteCode);
    }

    /**
     * 비동기 Redis 접속해서 데이터 삭제
     * @param key
     * @return
     */
    @Async
    protected void deleteRedis(String key){
        getRedisTemplate().delete(key);
    }







    protected abstract RedisTemplate<String,byte[]> getRedisTemplate();

}
