package com.inditex.backenddevtest.product.infrastructure;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimilarProductsFallBack implements SimilarProductsWebClient {

    @Override
    public List<String> getSimilarProductIds(String productId) {
        return List.of();
    }

    @Override
    public ProductDetailResponse getProductDetail(String productId) {
        return null;
    }
}
