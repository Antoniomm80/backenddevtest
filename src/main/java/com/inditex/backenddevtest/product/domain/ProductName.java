package com.inditex.backenddevtest.product.domain;

public record ProductName(String name) {
    public ProductName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
    }
}
