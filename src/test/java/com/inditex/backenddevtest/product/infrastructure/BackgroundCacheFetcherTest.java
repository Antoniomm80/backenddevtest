package com.inditex.backenddevtest.product.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.inditex.backenddevtest.IntegrationTest;
import com.inditex.backenddevtest.product.domain.ProductId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.wiremock.spring.InjectWireMock;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@IntegrationTest
class BackgroundCacheFetcherTest {

    @Autowired
    private BackgroundCacheFetcher backgroundCacheFetcher;

    @InjectWireMock
    private WireMockServer wireMockServer;

    @Autowired
    private CacheManager cacheManager;

    private Cache cache;

    @BeforeEach
    void setUp() {
        cache = cacheManager.getCache("productDetails");
        if (cache != null) {
            cache.clear();
        }
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("El background fetcher debe obtener el dato y meterlo en la cache")
    void shouldFetchProductAndCacheOnSuccess() {
        ProductId productId = new ProductId("1");
        String responseJson = """
                {"id": "1", "name": "Test Product", "price": 10, "availability": true}
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/product/1")).willReturn(aResponse().withStatus(200)
                                                                                       .withHeader("Content-Type", "application/json")
                                                                                       .withBody(responseJson)));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
               .untilAsserted(() -> {
                   wireMockServer.verify(getRequestedFor(urlPathEqualTo("/product/1")));
                   Cache.ValueWrapper cachedValue = cache.get("1");
                   assertThat(cachedValue).isNotNull();
                   assertThat(cachedValue.get()).isNotNull();
               });
    }

    @Test
    @DisplayName("No debe meter nada en cache si da 404")
    void shouldNotCacheWhenProductNotFound() {
        ProductId productId = new ProductId("404");

        wireMockServer.stubFor(get(urlPathEqualTo("/product/404")).willReturn(aResponse().withStatus(404)));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
               .untilAsserted(() -> wireMockServer.verify(getRequestedFor(urlPathEqualTo("/product/404"))));

        Cache.ValueWrapper cachedValue = cache.get("404");
        assertThat(cachedValue).isNull();
    }

    @Test
    @DisplayName("No debe meter nada en cache si se produce cualquier otro error")
    void shouldNotCacheOnOtherExceptions() {
        ProductId productId = new ProductId("500");

        wireMockServer.stubFor(get(urlPathEqualTo("/product/500")).willReturn(aResponse().withStatus(500)));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
               .untilAsserted(() -> wireMockServer.verify(getRequestedFor(urlPathEqualTo("/product/500"))));

        Cache.ValueWrapper cachedValue = cache.get("500");
        assertThat(cachedValue).isNull();
    }
}
