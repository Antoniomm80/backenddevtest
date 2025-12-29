package com.inditex.backenddevtest.product.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private final long expirationTime;
    private final long maximumSize;

    public CacheConfig(@Value("${product.api.cache.expiration.seconds}") long expirationTime, @Value("${product.api.cache.size}") long maximumSize) {
        this.expirationTime = expirationTime;
        this.maximumSize = maximumSize;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("productDetails", "similarIds");
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                       .expireAfterWrite(expirationTime, TimeUnit.SECONDS)
                       .maximumSize(maximumSize);
    }
}
