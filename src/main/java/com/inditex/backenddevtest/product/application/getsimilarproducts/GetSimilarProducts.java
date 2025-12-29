package com.inditex.backenddevtest.product.application.getsimilarproducts;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class GetSimilarProducts {
    private final SimilarProductsService similarProductsService;
    private final ExecutorService virtualThreadExecutor;

    public GetSimilarProducts(SimilarProductsService similarProductsService,
            @Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor) {
        this.similarProductsService = similarProductsService;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    public List<ProductDetail> handle(SimilarProductQuery query) {
        List<ProductId> similarProducts = similarProductsService.findSimilarProductsByProductId(new ProductId(query.productId()));

        List<CompletableFuture<Optional<ProductDetail>>> futures = similarProducts.stream()
                                                                                  .map(productId -> CompletableFuture.supplyAsync(
                                                                                          () -> similarProductsService.getProductDetailById(
                                                                                                  productId), virtualThreadExecutor))
                                                                                  .toList();

        return futures.stream()
                      .map(CompletableFuture::join)
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .toList();
    }
}
