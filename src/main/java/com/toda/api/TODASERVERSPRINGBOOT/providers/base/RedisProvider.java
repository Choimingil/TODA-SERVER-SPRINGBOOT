package com.toda.api.TODASERVERSPRINGBOOT.providers.base;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public abstract class RedisProvider extends AbstractProvider implements BaseProvider {
    /**
     * 비동기 처리로 Redis 접속
     * @param key
     * @return : Future<byte[]>
     */
    @Async
    protected Future<byte[]> getRedis(String key) {
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
    protected Future<List<byte[]>> getRedisList(List<String> keys) {
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
