package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class SimilarProductsServiceImpl implements SimilarProductsService {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsServiceImpl.class);
    private final SimilarProductsWebClient similarProductsWebClient;
    private final ParallelExecutor parallelExecutor;

    SimilarProductsServiceImpl(SimilarProductsWebClient similarProductsWebClient, ParallelExecutor parallelExecutor) {
        this.similarProductsWebClient = similarProductsWebClient;
        this.parallelExecutor = parallelExecutor;
    }

    @Override
    public List<ProductId> findSimilarProductsByProductId(ProductId productId) {
        return similarProductsWebClient.getSimilarProductIds(productId.id())
                                       .stream()
                                       .map(ProductId::new)
                                       .toList();
    }

    @Override
    public Optional<ProductDetail> getProductDetailById(ProductId productId) {
        ProductDetailResponse productDetailResponse = similarProductsWebClient.getProductDetail(productId.id());

        if (productDetailResponse == null) {
            return Optional.empty();
        }

        return Optional.of(ProductDetail.of(productDetailResponse.id(), productDetailResponse.name(), productDetailResponse.price(),
                productDetailResponse.availability()));
    }

    @Override

    public List<ProductDetail> getProductDetailsByIds(List<ProductId> productIds) {
        return parallelExecutor.executeInParallel(productIds, this::getProductDetailById);
    }
}
