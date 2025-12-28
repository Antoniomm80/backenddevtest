package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.application.getsimilarproducts.GetSimilarProducts;
import com.inditex.backenddevtest.product.application.getsimilarproducts.SimilarProductQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("product")
class SimilarProductsController {
    private final GetSimilarProducts getSimilarProducts;

    SimilarProductsController(GetSimilarProducts getSimilarProducts) {
        this.getSimilarProducts = getSimilarProducts;
    }

    @GetMapping("{productId}/similar")
    ResponseEntity<List<SimilarProductResponse>> getSimilarProducts(@PathVariable String productId) {
        List<SimilarProductResponse> response = getSimilarProducts.handle(new SimilarProductQuery(productId))
                                                                  .stream()
                                                                  .map(sp -> new SimilarProductResponse(sp.getId(), sp.getName(), sp.getPrice(),
                                                                          sp.isAvailable()))
                                                                  .toList();

        return ResponseEntity.ok(response);
    }
}
