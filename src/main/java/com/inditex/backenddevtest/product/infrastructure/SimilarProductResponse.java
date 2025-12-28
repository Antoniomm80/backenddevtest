package com.inditex.backenddevtest.product.infrastructure;

import java.math.BigDecimal;

public record SimilarProductResponse(String id, String name, BigDecimal price, boolean availability) {
}
