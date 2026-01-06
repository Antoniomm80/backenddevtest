package com.inditex.backenddevtest.product.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class BackgroundRestTemplateConfig {
    @Bean("backgroundRestTemplate")
    public RestTemplate backgroundRestTemplate(@Value("${product.api.background.timeout.seconds}") int timeoutSeconds,
            @Value("${product.api.background.read.seconds}") int readTimeoutSeconds) {
        return new RestTemplateBuilder().connectTimeout(Duration.ofSeconds(readTimeoutSeconds))
                                        .readTimeout(Duration.ofSeconds(timeoutSeconds))
                                        .build();
    }
}
