package com.inditex.backenddevtest.product.application.getsimilarproducts;

import com.inditex.backenddevtest.product.domain.ProductDetail;
import com.inditex.backenddevtest.product.domain.ProductDetailMother;
import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.SimilarProductsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static com.inditex.backenddevtest.product.domain.ProductIdsMother.someProductIds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class GetSimilarProductsTest {
    private final SimilarProductsService similarProductsService = Mockito.mock(SimilarProductsService.class);
    private final GetSimilarProducts getSimilarProducts = new GetSimilarProducts(similarProductsService);

    @Test
    @DisplayName("La consulta de productos similares por código de producto debe devolver una lista de los detalles de producto similares")
    void getSimilarProductsShouldReturnListOfSimilarProductDetails() {
        given(similarProductsService.findSimilarProductsByProductId(any())).willReturn(someProductIds());

        given(similarProductsService.getProductDetailById(new ProductId("2"))).willReturn(aDressProduct());
        given(similarProductsService.getProductDetailById(new ProductId("3"))).willReturn(aBlazerProduct());
        given(similarProductsService.getProductDetailById(new ProductId("4"))).willReturn(aBootsProduct());

        List<ProductDetail> similarProductDetails = getSimilarProducts.handle(new SimilarProductQuery("1"));

        assertThat(similarProductDetails).isNotEmpty()
                                         .hasSize(3)
                                         .containsExactly(ProductDetail.of("2", "Dress", new BigDecimal("19.99"), true),
                                                 ProductDetail.of("3", "Blazer", new BigDecimal("29.99"), false),
                                                 ProductDetail.of("4", "Boot", new BigDecimal("39.99"), true));
    }

    private ProductDetail aDressProduct() {
        return ProductDetailMother.someProductDetail()
                                  .withId("2")
                                  .withName("Dress")
                                  .withPrice(new BigDecimal("19.99"))
                                  .available()
                                  .build();
    }

    private ProductDetail aBlazerProduct() {
        return ProductDetailMother.someProductDetail()
                                  .withId("3")
                                  .withName("Blazer")
                                  .withPrice(new BigDecimal("29.99"))
                                  .unavailable()
                                  .build();
    }

    private ProductDetail aBootsProduct() {
        return ProductDetailMother.someProductDetail()
                                  .withId("4")
                                  .withName("Boot")
                                  .withPrice(new BigDecimal("39.99"))
                                  .available()
                                  .build();
    }

}