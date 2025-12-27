package com.inditex.backenddevtest.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {

    private static final int FIVE_SECONDS = 5;
    private static final int TEN_SECONDS = 10;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            FIVE_SECONDS,TimeUnit.SECONDS,
            TEN_SECONDS,TimeUnit.SECONDS,
                false
        );
    }
}
