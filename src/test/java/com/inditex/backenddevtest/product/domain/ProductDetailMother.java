package com.inditex.backenddevtest.product.domain;

import java.math.BigDecimal;

public class ProductDetailMother {
    private String id;
    private String name;
    private BigDecimal price;
    private boolean availability;

    public static ProductDetailMother someProductDetail() {
        return new ProductDetailMother().withId("1")
                                        .withName("Product 1")
                                        .withPrice(BigDecimal.TEN)
                                        .available();
    }

    public ProductDetailMother withId(String id) {
        this.id = id;
        return this;
    }

    public ProductDetailMother withName(String name) {
        this.name = name;
        return this;
    }

    public ProductDetailMother withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductDetailMother available() {
        this.availability = true;
        return this;
    }

    public ProductDetailMother unavailable() {
        this.availability = false;
        return this;
    }

    public ProductDetail build() {
        return ProductDetail.of(id, name, price, availability);
    }
}
