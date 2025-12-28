package com.inditex.backenddevtest.product.domain;

import java.util.List;

public class ProductIdsMother {
    public static List<ProductId> someProductIds() {
        return List.of(new ProductId("2"), new ProductId("3"), new ProductId("4"));
    }
}
