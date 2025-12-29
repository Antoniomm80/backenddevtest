package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Primary
class SimilarProductsServiceCache implements SimilarProductsService {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsServiceCache.class);

    private final SimilarProductsServiceImpl similarProductsServiceImpl;

    SimilarProductsServiceCache(SimilarProductsServiceImpl similarProductsServiceImpl) {
        this.similarProductsServiceImpl = similarProductsServiceImpl;
    }

    @Override
    public List<ProductId> findSimilarProductsByProductId(ProductId productId) {
        return similarProductsServiceImpl.findSimilarProductsByProductId(productId);
    }

    @Override
    @Cacheable(value = "productDetails", key = "#productId.id()", unless = "#result == null")
    public Optional<ProductDetail> getProductDetailById(ProductId productId) {
        log.debug("CACHE MISS - Product id: {} not cached, fetching from upstream", productId.id());

        return similarProductsServiceImpl.getProductDetailById(productId);
    }
}
