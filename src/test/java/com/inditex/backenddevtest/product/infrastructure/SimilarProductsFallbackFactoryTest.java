package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.product.domain.ProductId;
import com.inditex.backenddevtest.product.domain.ProductNotFoundException;
import com.inditex.backenddevtest.product.domain.ProductServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

class SimilarProductsFallbackFactoryTest {
    private final BackgroundCacheFetcher backgroundCacheFetcher = Mockito.mock(BackgroundCacheFetcher.class);
    private final SimilarProductsFallbackFactory similarProductsFallbackFactory = new SimilarProductsFallbackFactory(backgroundCacheFetcher);

    @Test
    @DisplayName("Una excepción timeout programará un fetch en segundo plano y devolverá nulo")
    void givenTimeOutExceptionFactoryShouldCreateAFallbackThatFetchesTheResourceInTheBackgroundAndReturnNull() {
        SimilarProductsWebClient similarProductsFallback = similarProductsFallbackFactory.create(new SocketTimeoutException("Timeout"));

        ProductDetailResponse productDetail = similarProductsFallback.getProductDetail("1");

        then(backgroundCacheFetcher).should()
                                    .triggerBackgroundFetch(new ProductId("1"));
        assertThat(productDetail).isNull();
    }

    @Test
    @DisplayName("Una excepción product not found devolverá nulo")
    void givenTimeOutExceptionFactoryShouldReturnNull() {
        SimilarProductsWebClient similarProductsFallback = similarProductsFallbackFactory.create(new ProductNotFoundException("Product not found"));

        ProductDetailResponse productDetail = similarProductsFallback.getProductDetail("1");

        then(backgroundCacheFetcher).shouldHaveNoInteractions();
        assertThat(productDetail).isNull();
    }

    @Test
    @DisplayName("Una excepción de cualquier otro tipo lanzará una ProductServiceException")
    void givenAnyOtherExceptionFactoryShouldThrowProductServiceException() {
        SimilarProductsWebClient similarProductsFallback = similarProductsFallbackFactory.create(new RuntimeException("A random exception"));
        
        assertThatThrownBy(() -> {
            ProductDetailResponse productDetail = similarProductsFallback.getProductDetail("1");
        }).isInstanceOf(ProductServiceException.class);
    }

}