package com.inditex.backenddevtest.product.infrastructure;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.inditex.backenddevtest.E2ETest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.InjectWireMock;

import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@E2ETest
class SimilarProductsControllerE2ETest {
    private static final String PRODUCT_ID_UNDER_TEST = "1";

    @Autowired
    private MockMvc mockMvc;

    @InjectWireMock()
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer.stubFor(get(urlPathTemplate("/product/{productId}/similarids")).withPathParam("productId", equalTo(PRODUCT_ID_UNDER_TEST))
                                                                                      .willReturn(aResponse().withHeader("Content-Type",
                                                                                                                     "application/json")
                                                                                                             .withBodyFile("similar-ids.json")));

        IntStream.range(2, 5)
                 .forEach(i -> wireMockServer.stubFor(
                         get(urlPathTemplate("/product/{productId}")).withPathParam("productId", equalTo(String.valueOf(i)))
                                                                     .willReturn(aResponse().withHeader("Content-Type", "application/json")
                                                                                            .withBodyFile(
                                                                                                    String.format("product-detail-%s.json", i)))));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("Dada una petición web para obtener productos similares a un id, debe devolver la lista de detalles de productos similares")
    void givenWebCallToGetSimilarProductsShouldReturnListOfSimilarProductDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/product/{productId}/similar", PRODUCT_ID_UNDER_TEST))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$", hasSize(3)))
               .andExpect(jsonPath("$[0].id", is("2")))
               .andExpect(jsonPath("$[0].name", is("Dress")))
               .andExpect(jsonPath("$[0].price", is(19.99)))
               .andExpect(jsonPath("$[0].availability", is(true)))
               .andExpect(jsonPath("$[1].id", is("3")))
               .andExpect(jsonPath("$[1].name", is("Blazer")))
               .andExpect(jsonPath("$[1].price", is(29.99)))
               .andExpect(jsonPath("$[1].availability", is(false)))
               .andExpect(jsonPath("$[2].id", is("4")))
               .andExpect(jsonPath("$[2].name", is("Boots")))
               .andExpect(jsonPath("$[2].price", is(39.99)))
               .andExpect(jsonPath("$[2].availability", is(true)));
    }
}