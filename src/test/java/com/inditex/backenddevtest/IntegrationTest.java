package com.inditex.backenddevtest;

import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = { "product.api.url=${wiremock.server.baseUrl}" })
@EnableWireMock(@ConfigureWireMock)
public @interface IntegrationTest {
}
