package com.drapson.springauthtutorial.adapters.out.redis;

import com.drapson.springauthtutorial.application.TempUserDataPort;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedisTempUserDataAdapter implements TempUserDataPort {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTempUserDataAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, Object data, Duration timeout) {
        redisTemplate.opsForValue().set(key, data, timeout);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
