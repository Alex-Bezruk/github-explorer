package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.exception.BadRequestException;
import com.bezruk.github.explorer.exception.UnexpectedHeaderException;
import com.bezruk.github.explorer.model.ApplicationError;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import com.bezruk.github.explorer.stub.GitHubApiStub;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RepositoryResourceIntegrationTest {

    private static final GitHubApiStub apiStub = new GitHubApiStub();

    @BeforeAll
    static void startWiremock() {
        apiStub.startWireMock();
    }

    @AfterAll
    static void stopWiremock() {
        apiStub.stopWireMock();
    }

    @BeforeEach
    void resetWiremock() {
        apiStub.reset();
    }

    @Test
    public void testGetRepositories() {
        List<String> repositories = List.of("repo1", "repo2");

        apiStub.stubRepositoriesEndpoint("testName", repositories);
        apiStub.stubBranchesEndpoint("testName", repositories);

        TypeRef<List<Repository>> listOfRepositories = new TypeRef<>() {
        };
        List<Repository> actualRepositories =
                given()
                        .accept("application/json")
                        .param("username", "testName")
                        .when().get("/repositories")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().body().as(listOfRepositories);


        Set<String> expectedBranches = Set.of("branch1", "branch2");
        Set<String> actualBranches = actualRepositories.stream()
                .flatMap(r -> r.getBranches().stream())
                .map(Branch::name)
                .collect(Collectors.toSet());

        assertEquals(expectedBranches, actualBranches);

        actualRepositories.stream()
                .map(Repository::getName)
                .forEach(name -> assertTrue(repositories.contains(name)));
    }

    @Test
    void testUserNotFound() {
        String userName = RandomStringUtils.randomAlphabetic(15);
        apiStub.stubUserNotFound(userName);

        ApplicationError error = given()
                .accept("application/json")
                .param("username", userName)
                .when().get("/repositories")
                .then().extract().as(ApplicationError.class);

        String expectedUrl = String.format("http://0.0.0.0:8084/users/%s/repos", userName);
        String expectedMessage = String.format("GithubRequest failed. Request url: %s", expectedUrl);

        ApplicationException expectedException = new BadRequestException(expectedMessage);
        ApplicationError expectedError = ApplicationError.fromException(expectedException);

        assertEquals(expectedError.getMessage(), error.getMessage());
    }

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
}
