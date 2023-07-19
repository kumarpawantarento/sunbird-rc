package dev.sunbirdrc.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    public void putValue(String key, Integer value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void putValueWithExpireTime(String key, Integer value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Integer getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
