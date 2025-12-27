package com.inditex.backenddevtest.product.domain;

import java.math.BigDecimal;

public record ProductDetail(
    ProductId id,
    ProductName name,
    ProductPrice price,
    boolean availability
) {
    public static ProductDetail of(String id, String name, BigDecimal price, boolean availability) {
        return new ProductDetail(new ProductId(id), new ProductName(name), new ProductPrice(price), availability);
    }
}
