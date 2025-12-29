package com.inditex.backenddevtest.product.domain;

public class ProductServiceException extends RuntimeException {
    private final String productId;

    public ProductServiceException(String productId, Throwable cause) {
        super("Product service error for: " + productId, cause);
        this.productId = productId;
    }

    public ProductServiceException(String productId, String message) {
        super("Product service error for: " + productId + " - " + message);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
