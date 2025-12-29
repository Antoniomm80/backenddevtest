package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class SimilarProductsServiceImpl implements SimilarProductsService {
    private static final Logger log = LoggerFactory.getLogger(SimilarProductsServiceImpl.class);
    private final SimilarProductsWebClient similarProductsWebClient;

    SimilarProductsServiceImpl(SimilarProductsWebClient similarProductsWebClient) {
        this.similarProductsWebClient = similarProductsWebClient;
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
        try {
            ProductDetailResponse productDetailResponse = similarProductsWebClient.getProductDetail(productId.id());
            if (productDetailResponse == null) {
                return Optional.empty();
            }
            return Optional.of(ProductDetail.of(productDetailResponse.id(), productDetailResponse.name(), productDetailResponse.price(),
                    productDetailResponse.availability()));
        } catch (ProductNotFoundException pnfe) {
            log.info("Product not found: {}", productId.id());
            return Optional.empty();
        } catch (ProductServiceException pse) {
            log.error("Product service error for: {}", productId.id(), pse);
            return Optional.empty();
        } catch (Exception fe) {
            log.error("Connection error for: {}", productId.id(), fe);
            return Optional.empty();
        }
    }
}
