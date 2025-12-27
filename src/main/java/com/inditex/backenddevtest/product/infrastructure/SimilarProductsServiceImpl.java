package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SimilarProductsServiceImpl implements SimilarProductsService {
    private final SimilarProductsWebClient similarProductsWebClient;

    SimilarProductsServiceImpl(SimilarProductsWebClient similarProductsWebClient) {
        this.similarProductsWebClient = similarProductsWebClient;
    }

    @Override
    public List<ProductId> findSimilarProductsByProductId(ProductId productId) {
        return similarProductsWebClient.getSimilarProductIds(productId.getId())
                                       .stream()
                                       .map(ProductId::new)
                                       .toList();
    }

    @Override
    public ProductDetail getProductDetailById(ProductId productId) {
        ProductDetailResponse productDetailResponse = similarProductsWebClient.getProductDetail(productId.getId());
        return ProductDetail.of(productDetailResponse.id(), productDetailResponse.name(), productDetailResponse.price(),
                productDetailResponse.availability());
    }
}
