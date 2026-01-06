package com.inditex.backenddevtest.product.application.getsimilarproducts;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSimilarProducts {
    private final SimilarProductsService similarProductsService;

    public GetSimilarProducts(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;

    }

    public List<ProductDetail> handle(SimilarProductQuery query) {
        List<ProductId> similarProducts = similarProductsService.findSimilarProductsByProductId(new ProductId(query.productId()));
        return similarProductsService.getProductDetailsByIds(similarProducts);
    }
}
