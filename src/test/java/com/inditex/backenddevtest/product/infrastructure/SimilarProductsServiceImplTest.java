package com.inditex.backenddevtest.product.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.inditex.backenddevtest.IntegrationTest;
import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wiremock.spring.InjectWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class SimilarProductsServiceImplTest {
    private static final String PRODUCT_ID_UNDER_TEST = "1";
    private static final String SIMILAR_PRODUCT_ID_UNDER_TEST = "4";
    @Autowired
    private SimilarProductsServiceImpl similarProductsService;

    @InjectWireMock()
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer.stubFor(get(urlPathTemplate("/product/{productId}/similarids")).withPathParam("productId", equalTo(PRODUCT_ID_UNDER_TEST))
                                                                                      .willReturn(aResponse().withHeader("Content-Type",
                                                                                                                     "application/json")
                                                                                                             .withBodyFile("similar-ids.json")));

        wireMockServer.stubFor(get(urlPathTemplate("/product/{productId}")).withPathParam("productId", equalTo(SIMILAR_PRODUCT_ID_UNDER_TEST))
                                                                           .willReturn(aResponse().withHeader("Content-Type", "application/json")
                                                                                                  .withBodyFile("product-detail-4.json")));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

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