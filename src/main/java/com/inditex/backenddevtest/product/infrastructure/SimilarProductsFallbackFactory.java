package com.inditex.backenddevtest.product.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
public class SimilarProductsFallbackFactory implements FallbackFactory<SimilarProductsWebClient> {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsFallbackFactory.class);

    private final BackgroundCacheFetcher backgroundCacheFetcher;

    public SimilarProductsFallbackFactory(BackgroundCacheFetcher backgroundCacheFetcher) {
        this.backgroundCacheFetcher = backgroundCacheFetcher;
    }

    @Override
    public SimilarProductsWebClient create(Throwable cause) {
        return new SimilarProductsWebClient() {
            @Override
            public List<String> getSimilarProductIds(String productId) {
                log.warn("Fallback for getSimilarProductIds, product {}: {}", productId, cause.getClass()
                                                                                              .getSimpleName());
                return List.of();
            }

            @Override
            public ProductDetailResponse getProductDetail(String productId) {
                if (isTimeoutException(cause)) {
                    backgroundCacheFetcher.triggerBackgroundFetch(new com.inditex.backenddevtest.product.domain.ProductId(productId));
                }
                return null;
            }
        };
    }

    private boolean isTimeoutException(Throwable e) {
        if (e == null) {
            return false;
        }
        if (e instanceof TimeoutException || e instanceof SocketTimeoutException) {
            return true;
        }
        return isTimeoutException(e.getCause());
    }
}
