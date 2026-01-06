package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Component
public class BackgroundCacheFetcher {
    private static final Logger log = LoggerFactory.getLogger(BackgroundCacheFetcher.class);
    private static final String CACHE_NAME = "productDetails";

    private final RestTemplate restTemplate;
    private final String productApiUrl;
    private final Cache cache;
    private final ExecutorService virtualThreadExecutor;
    private final ConcurrentHashMap<String, CompletableFuture<Void>> inFlightFetches = new ConcurrentHashMap<>();

    public BackgroundCacheFetcher(@Qualifier("backgroundRestTemplate") RestTemplate restTemplate, CacheManager cacheManager,
            @Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor, @Value("${product.api.url}") String productApiUrl) {
        this.restTemplate = restTemplate;
        this.cache = cacheManager.getCache(CACHE_NAME);
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.productApiUrl = productApiUrl;
    }

    public void triggerBackgroundFetch(ProductId productId) {
        String cacheKey = productId.id();

        inFlightFetches.computeIfAbsent(cacheKey, key -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> fetchAndCache(cacheKey), virtualThreadExecutor);

            future.whenComplete((result, throwable) -> inFlightFetches.remove(cacheKey));

            return future;
        });
    }

    private void fetchAndCache(String productId) {
        try {
            String url = productApiUrl + "/product/" + productId;
            ResponseEntity<ProductDetailResponse> response = restTemplate.getForEntity(url, ProductDetailResponse.class);

            if (response.getStatusCode()
                        .is2xxSuccessful() && response.getBody() != null) {
                ProductDetailResponse body = response.getBody();
                cache.put(productId, Optional.of(ProductDetail.of(body.id(), body.name(), body.price(), body.availability())));
            }
        } catch (Exception e) {
            log.warn("Background fetch failed for product {}: {}", productId, e.getMessage());
        }
    }
}
