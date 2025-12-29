package com.inditex.backenddevtest.product.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface SimilarProductsService {
    List<ProductId> findSimilarProductsByProductId(ProductId productId);

    Optional<ProductDetail> getProductDetailById(ProductId productId);
}
