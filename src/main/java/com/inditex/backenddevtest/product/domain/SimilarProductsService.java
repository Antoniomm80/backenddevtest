package com.inditex.backenddevtest.product.domain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SimilarProductsService {
    List<ProductId> findSimilarProductsByProductId(ProductId productId);
    ProductDetail getProductDetailById(ProductId productId);
}
