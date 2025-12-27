package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimilarProductsServiceImplTest {
    private static final String PRODUCT_ID_UNDER_TEST = "1";
    private static final String SIMILAR_PRODUCT_ID_UNDER_TEST = "4";
    @Autowired
    private SimilarProductsServiceImpl similarProductsService;

    @Test
    @DisplayName("La llamada a productos similares por id debe devolver una lista de ids de productos similares ordenados")
    void findSimilarProductsByProductIdShouldReturnListOfSimilarProductIdsSorted() {
        ProductId productId = new ProductId(PRODUCT_ID_UNDER_TEST);

        List<ProductId> similarProducts = similarProductsService.findSimilarProductsByProductId(productId);

        assertThat(similarProducts).isNotEmpty()
                                   .hasSize(3)
                                   .containsExactly(new ProductId("2"), new ProductId("3"), new ProductId("4"));
    }

    @Test
    @DisplayName("La llamada al detalle de un producto por id, debe devolver su nombre, su precio y disponibilidad")
    void getProductDetailByIdShouldReturnProductDetailFullyPopulated() {
        ProductDetail productDetail = similarProductsService.getProductDetailById(new ProductId(SIMILAR_PRODUCT_ID_UNDER_TEST));

        assertThat(productDetail).isNotNull();
        assertThat(productDetail.name()
                                .name()).isEqualTo("Boots");
        assertThat(productDetail.price()
                                .price()).isEqualByComparingTo("39.99");
        assertThat(productDetail.availability()).isTrue();
    }
}