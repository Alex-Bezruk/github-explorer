package com.bezruk.github.explorer.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class RepositoryResourceTest {
    @Test
    void testEndpointCallSuccess() {
        given()
                .accept("application/json")
                .param("username", "Alex-Bezruk")
                .when().get("/repositories")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    void testHeaderValidation() {
        given()
                .accept("application/xml")
                .param("username", "Alex-Bezruk")
                .when().get("/repositories")
                .then()
                .statusCode(406)
                .body(is("{\"status\":406,\"message\":\"Unsupported media type: application/xml\"}"));
    }

}