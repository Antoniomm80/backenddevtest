package com.inditex.backenddevtest.product.domain;

import java.math.BigDecimal;

public record ProductPrice(BigDecimal price) {
    public ProductPrice {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be negative or zero");
        }
    }
}
