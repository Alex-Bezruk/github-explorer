package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import com.bezruk.github.explorer.stub.GitHubApiStub;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RepositoryResourceIntegrationTest {

    private final GitHubApiStub apiStub = new GitHubApiStub();

    @Test
    public void testGetRepositories() {
        apiStub.startWireMock();
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

        actualRepositories.forEach(repository -> {
            assertTrue(repositories.contains(repository.getName()));
        });

        apiStub.stopWireMock();
    }
}
