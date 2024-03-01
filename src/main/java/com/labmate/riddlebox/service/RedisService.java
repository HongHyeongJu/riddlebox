package com.labmate.riddlebox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public boolean validateCode(String key, String code) {
        String storedCode = getValues(key);
        return storedCode != null && storedCode.equals(code);
    }

    public boolean checkExistsValue(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    public void deleteCode(String key) {
        redisTemplate.delete(key);
    }
}
