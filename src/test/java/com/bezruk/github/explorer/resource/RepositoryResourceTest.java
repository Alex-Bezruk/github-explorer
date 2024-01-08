package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.exception.BadRequestException;
import com.bezruk.github.explorer.exception.UnexpectedHeaderException;
import com.bezruk.github.explorer.model.ApplicationError;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
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

        ApplicationException expectedException = new UnexpectedHeaderException("Unsupported media type: application/xml");
        ApplicationError expectedError = ApplicationError.fromException(expectedException);

        assertEquals(error, expectedError);
    }

    @Test
    void testUserNotFound() {
        String username = RandomStringUtils.randomAlphanumeric(15);
        String expectedUrl = String.format("https://api.github.com/users/%s/repos", username);
        ApplicationError error = given()
                .accept("application/json")
                .param("username", username)
                .when().get("/repositories")
                .then().extract().as(ApplicationError.class);

        String expectedMessage = String.format("Requested User was not found." +
                " GithubApi responded with status 404. RequestURL: %s", expectedUrl);

        ApplicationException expectedException = new BadRequestException(expectedMessage);
        ApplicationError expectedError = ApplicationError.fromException(expectedException);

        assertEquals(error, expectedError);
    }
}