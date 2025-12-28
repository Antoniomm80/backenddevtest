package com.inditex.backenddevtest.product.domain;

public record ProductId(String id) {
    public ProductId {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id cannot be null or blank");
        }
    }
}
