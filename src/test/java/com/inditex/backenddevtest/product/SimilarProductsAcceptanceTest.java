package com.inditex.backenddevtest.product;

import com.inditex.backenddevtest.AcceptanceTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;

@AcceptanceTest
class SimilarProductsAcceptanceTest {
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5000;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @DisplayName("Happy Path. Debe devolver la lista completa con todos los campos")
    @Test
    void shouldReturnSimilarProductsWithFullDetails() {
        given().accept(ContentType.JSON)
               .when()
               .get("/product/{productId}/similar", 1)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("$.size()", is(3))
               .body("[0].id", is("2"))
               .body("[0].name", is("Dress"))
               .body("[0].price", is(19.99f))
               .body("[0].availability", is(true))
               .body("[1].id", is("3"))
               .body("[1].name", is("Blazer"))
               .body("[1].price", is(29.99f))
               .body("[1].availability", is(false))
               .body("[2].id", is("4"))
               .body("[2].name", is("Boots"))
               .body("[2].price", is(39.99f))
               .body("[2].availability", is(true));
    }
    
    @DisplayName("Ante una petición con algunos de los productos lanzando error not found. Debe fallar gracefully y devolver el resto")
    @Test
    void shouldReturnPartialResultsWhen404ErrorIsReturnedFromUpstreamServer() {
        given().accept(ContentType.JSON)
               .when()
               .get("/product/{productId}/similar", 4)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("$.size()", is(2))
               .body("[0].id", is("1"))
               .body("[0].name", is("Shirt"))
               .body("[0].price", is(9.99f))
               .body("[0].availability", is(true))
               .body("[1].id", is("2"))
               .body("[1].name", is("Dress"))
               .body("[1].price", is(19.99f))
               .body("[1].availability", is(true));
    }

    @DisplayName("Ante una petición con algunos de los productos lanzando error 500. Debe fallar gracefully y devolver el resto")
    @Test
    void shouldReturnPartialResultsWhen500ErrorIsReturnedFromUpstreamServer() {
        given().accept(ContentType.JSON)
               .when()
               .get("/product/{productId}/similar", 5)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("$.size()", is(2))
               .body("[0].id", is("1"))
               .body("[0].name", is("Shirt"))
               .body("[0].price", is(9.99f))
               .body("[0].availability", is(true))
               .body("[1].id", is("2"))
               .body("[1].name", is("Dress"))
               .body("[1].price", is(19.99f))
               .body("[1].availability", is(true));
    }

    @DisplayName("Cuando se produce un error por timeout, el sistema reintentará obtener el recurso con un timeout más alto en segundo plano")
    @Test
    void shouldPopulateCacheWithSlowProductsViaBackgroundFetcher() {
        int initialSize = given().accept(ContentType.JSON)
                                 .when()
                                 .get("/product/{productId}/similar", 3)
                                 .then()
                                 .statusCode(200)
                                 .extract()
                                 .jsonPath()
                                 .getList("$")
                                 .size();

        await().atMost(15, TimeUnit.SECONDS)
               .pollInterval(2, TimeUnit.SECONDS)
               .untilAsserted(() -> {
                   List<?> products = given().accept(ContentType.JSON)
                                             .when()
                                             .get("/product/{productId}/similar", 3)
                                             .then()
                                             .statusCode(200)
                                             .extract()
                                             .jsonPath()
                                             .getList("$");

                   assertThat(products.size()).isGreaterThan(initialSize);
               });
    }
}
