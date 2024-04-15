package com.labmate.riddlebox.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList("gameDataCache", "gameInfoCache","gameStoryCache"));

        // gameDataCache 설정
        Caffeine<Object, Object> gameDataCacheBuilder = Caffeine.newBuilder()
                .expireAfterWrite(360, TimeUnit.MINUTES) // 캐시 만료 시간
                .maximumSize(1000); // 캐시 최대 크기
        cacheManager.registerCustomCache("gameDataCache", gameDataCacheBuilder.build());

        // gameInfoCache 설정
        Caffeine<Object, Object> gameInfoCacheBuilder = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES) // 짧은 만료 시간
                .maximumSize(100); // 더 작은 크기
        cacheManager.registerCustomCache("gameInfoCache", gameInfoCacheBuilder.build());

        // gameInfoCache 설정
        Caffeine<Object, Object> gameStoryCacheBuilder = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES) // 짧은 만료 시간
                .maximumSize(100); // 더 작은 크기
        cacheManager.registerCustomCache("gameStoryCache", gameStoryCacheBuilder.build());



        return cacheManager;
    }


}
