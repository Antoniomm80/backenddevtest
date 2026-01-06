package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.ProductNotFoundException;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
class SimilarProductsServiceCache implements SimilarProductsService {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsServiceCache.class);
    private static final String CACHE_NAME = "productDetails";

    private final SimilarProductsServiceImpl similarProductsServiceImpl;
    private final ParallelExecutor parallelExecutor;
    private final Cache cache;

    SimilarProductsServiceCache(SimilarProductsServiceImpl similarProductsServiceImpl, ParallelExecutor parallelExecutor,
            CacheManager cacheManager) {
        this.similarProductsServiceImpl = similarProductsServiceImpl;
        this.parallelExecutor = parallelExecutor;
        this.cache = cacheManager.getCache(CACHE_NAME);
    }

    @Override
    public List<ProductId> findSimilarProductsByProductId(ProductId productId) {
        return similarProductsServiceImpl.findSimilarProductsByProductId(productId);
    }

    @Override
    public Optional<ProductDetail> getProductDetailById(ProductId productId) {
        String cacheKey = productId.id();

        Cache.ValueWrapper cachedValue = cache.get(cacheKey);
        if (cachedValue != null) {
            return (Optional<ProductDetail>) cachedValue.get();
        }
        try {
            log.debug("Cache miss, Fetching product detail for {}", productId);
            Optional<ProductDetail> result = similarProductsServiceImpl.getProductDetailById(productId);
            cache.put(cacheKey, result);
            return result;
        } catch (ProductNotFoundException e) {
            Optional<ProductDetail> emptyResult = Optional.empty();
            cache.put(cacheKey, emptyResult);
            return emptyResult;

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ProductDetail> getProductDetailsByIds(List<ProductId> productIds) {
        return parallelExecutor.executeInParallel(productIds, this::getProductDetailById);
    }
}
