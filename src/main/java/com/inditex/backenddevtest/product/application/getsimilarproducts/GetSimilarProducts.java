package com.inditex.backenddevtest.product.application.getsimilarproducts;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetSimilarProducts {
    private final SimilarProductsService similarProductsService;

    public GetSimilarProducts(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    public List<ProductDetail> handle(SimilarProductQuery query) {
        List<ProductId> similarProducts = similarProductsService.findSimilarProductsByProductId(new ProductId(query.productId()));

        return similarProducts.stream()
                              .map(similarProductsService::getProductDetailById)
                              .filter(Optional::isPresent)
                              .map(Optional::get)
                              .toList();
    }
}
