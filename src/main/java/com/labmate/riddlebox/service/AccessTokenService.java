package com.labmate.riddlebox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AccessTokenService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AccessTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveAccessToken(String userId, String accessToken, Duration duration) {
        redisTemplate.opsForValue().set(buildKey(userId), accessToken, duration);
    }

    public String getAccessToken(String userId) {
        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    private String buildKey(String userId) {
        return "access_token:" + userId;
    }
}
