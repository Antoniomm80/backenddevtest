package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BackgroundCacheFetcherTest {

    private static final String API_URL = "http://localhost:3001";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private CacheManager cacheManager;
    private Cache cache;
    private ExecutorService virtualThreadExecutor;
    private BackgroundCacheFetcher backgroundCacheFetcher;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

        given(cacheManager.getCache("productDetails")).willReturn(cache);
        backgroundCacheFetcher = new BackgroundCacheFetcher(restTemplate, cacheManager, virtualThreadExecutor, API_URL);
    }

    @Test
    @DisplayName("El background fetcher debe obtener el dato y meterlo en la cache")
    void shouldFetchProductAndCacheOnSuccess() {
        ProductId productId = new ProductId("1");
        String responseJson = """
                {"id": "1", "name": "Test Product", "price": 10, "availability": true}
                """;

        mockServer.expect(requestTo(API_URL + "/product/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    mockServer.verify();
                    verify(cache).put(eq("1"), any(Optional.class));
                });
    }

    @Test
    @DisplayName("No debe meter nada en cache si da 404")
    void shouldNotCacheWhenProductNotFound() {
        ProductId productId = new ProductId("404");

        mockServer.expect(requestTo(API_URL + "/product/404"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> mockServer.verify());

        verify(cache, never()).put(eq("404"), any());
    }

    @Test
    @DisplayName("No debe meter nada en cache si se produce cualquier otro error")
    void shouldNotCacheOnOtherExceptions() {
        ProductId productId = new ProductId("500");

        mockServer.expect(requestTo(API_URL + "/product/500"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        backgroundCacheFetcher.triggerBackgroundFetch(productId);

        await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() -> mockServer.verify());

        verify(cache, never()).put(eq("500"), any());
    }
}
