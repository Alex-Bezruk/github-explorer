package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.model.ApplicationError;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class RepositoryResourceTest {
//    @Test
//    void testEndpointCallSuccess() {
//        given()
//                .accept("application/json")
//                .param("username", "Alex-Bezruk")
//                .when().get("/repositories")
//                .then()
//                .statusCode(200)
//                .body(is("[]"));
//    }

    @Test
    void testHeaderValidation() {
        ApplicationError error = given()
                .accept("application/xml")
                .param("username", "Alex-Bezruk")
                .when().get("/repositories")
                .then().extract().as(ApplicationError.class);

        ApplicationException expectedException = new ApplicationException(406, "Unsupported media type: application/xml");
        ApplicationError expectedError = ApplicationError.fromException(expectedException);

        assertEquals(error, expectedError);
    }

}