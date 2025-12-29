package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-api", url = "${product.api.url}", configuration = FeignConfig.class, fallback = SimilarProductsFallBack.class)
public interface SimilarProductsWebClient {

    @GetMapping("/product/{productId}/similarids")
    List<String> getSimilarProductIds(@PathVariable("productId") String productId);

    @GetMapping("/product/{productId}")
    ProductDetailResponse getProductDetail(@PathVariable("productId") String productId);
}
