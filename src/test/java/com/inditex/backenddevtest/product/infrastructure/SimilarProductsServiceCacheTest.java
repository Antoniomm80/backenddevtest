package com.inditex.backenddevtest.product.infrastructure;

import com.inditex.backenddevtest.IntegrationTest;
import com.inditex.backenddevtest.product.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@IntegrationTest
class SimilarProductsServiceCacheTest {
    @Autowired
    private SimilarProductsService similarProductsService;
    @Autowired
    private SimilarProductsServiceImpl similarProductsServiceImpl;
    @Autowired
    private CacheManager cacheManager;
    private SimilarProductsServiceImpl mockSimilarProductsServiceImpl;

    @BeforeEach
    void setUp() {
        mockSimilarProductsServiceImpl = mock(SimilarProductsServiceImpl.class);
        ReflectionTestUtils.setField(similarProductsService, "similarProductsServiceImpl", mockSimilarProductsServiceImpl);
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(similarProductsService, "similarProductsServiceImpl", similarProductsServiceImpl);
        cacheManager.getCacheNames()
                    .forEach(cacheName -> cacheManager.getCache(cacheName)
                                                      .clear());
    }

    @Test
    @DisplayName("Llamadas consecutivas a getProductDetailById suponen un hit de cache si el dato no es vacio")
    void givenConsecutiveToGetProductDetailByIdCallsShouldBeCacheHit() {
        given(mockSimilarProductsServiceImpl.getProductDetailById(new ProductId("1"))).willReturn(Optional.of(mock(ProductDetail.class)));
        similarProductsService.getProductDetailById(new ProductId("1"));
        similarProductsService.getProductDetailById(new ProductId("1"));

        then(mockSimilarProductsServiceImpl).should()
                                            .getProductDetailById(new ProductId("1"));
    }

    @Test
    @DisplayName("Llamadas consecutivas a getProductDetailById suponen un hit de cache si se recibe un Product Not Found Exception")
    void givenConsecutiveToGetProductDetailByIdCallsWithProductNotFoundExceptionShouldBeCacheHit() {
        given(mockSimilarProductsServiceImpl.getProductDetailById(new ProductId("1"))).willThrow(new ProductNotFoundException("Product not found"));
        similarProductsService.getProductDetailById(new ProductId("1"));
        similarProductsService.getProductDetailById(new ProductId("1"));

        then(mockSimilarProductsServiceImpl).should(times(1))
                                            .getProductDetailById(new ProductId("1"));
    }

    @Test
    @DisplayName("Llamadas consecutivas a getProductDetailById NO suponen un hit de cache si se recibe una Excepcion del upstream server")
    void givenConsecutiveToGetProductDetailByIdCallsWithProductServiceExceptionShouldBeCacheMiss() {
        given(mockSimilarProductsServiceImpl.getProductDetailById(new ProductId("1"))).willThrow(
                new ProductServiceException("Upstream server error", new RuntimeException()));
        similarProductsService.getProductDetailById(new ProductId("1"));
        similarProductsService.getProductDetailById(new ProductId("1"));

        then(mockSimilarProductsServiceImpl).should(times(2))
                                            .getProductDetailById(new ProductId("1"));
    }
}