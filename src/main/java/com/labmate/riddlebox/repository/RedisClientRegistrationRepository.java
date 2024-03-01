package com.labmate.riddlebox.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Duration;

@Repository
public class RedisClientRegistrationRepository implements ClientRegistrationRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisClientRegistrationRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * 구체적인 OAuth 2.0 클라이언트 등록 정보는 ClientRegistration 객체에 포함되어 관리됨
     *ClientRegistration 객체는 OAuth 2.0 클라이언트의 구성 정보를 포함하는 스프링 시큐리티의 클래스로,
     * 클라이언트 ID, 클라이언트 시크릿, 토큰 엔드포인트, 리다이렉션 URI, 스코프 등 OAuth 클라이언트 등록에 필요한 모든 정보를 포함함
     */

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String clientRegistrationJson = operations.get(getKey(registrationId));
        if (clientRegistrationJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(clientRegistrationJson, ClientRegistration.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing client registration", e);
        }
    }

    public void saveClientRegistration(String registrationId, ClientRegistration clientRegistration) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        try {
            String clientRegistrationJson = objectMapper.writeValueAsString(clientRegistration);
            operations.set(getKey(registrationId), clientRegistrationJson, Duration.ofDays(30)); // Example expiration: 30 days
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing client registration", e);
        }
    }

    private String getKey(String registrationId) {
        return "oauth2:client:registration:" + registrationId;
    }

}
