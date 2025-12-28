package com.inditex.backenddevtest.product.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductDetail {
    private ProductId id;
    private ProductName name;
    private ProductPrice price;
    private boolean availability;

    public ProductDetail(ProductId id, ProductName name, ProductPrice price, boolean availability) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availability = availability;
    }

    public static ProductDetail of(String id, String name, BigDecimal price, boolean availability) {
        return new ProductDetail(new ProductId(id), new ProductName(name), new ProductPrice(price), availability);
    }

    public String getId() {
        return id.id();
    }

    public String getName() {
        return name.name();
    }

    public BigDecimal getPrice() {
        return price.price();
    }

    public boolean isAvailable() {
        return availability;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        ProductDetail that = (ProductDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
