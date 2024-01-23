package com.bezruk.github.explorer.client;

import com.bezruk.github.explorer.exception.NotFoundException;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Commit;
import com.bezruk.github.explorer.model.Owner;
import com.bezruk.github.explorer.model.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GithubClientTest {

    @Test
    void whenUserWithProvidedNameExistsShouldFetchRepositoriesSuccessfully() {
        String userName = "Alex-Bezruk-Bezruk-333";
        String uri = "/users/{userName}/repos";
        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);


        Repository repository = new Repository();
        repository.setName("repo");
        Repository repository1 = new Repository();
        repository1.setName("repo1");
        ResponseEntity<List<Repository>> responseEntity = ResponseEntity.ok()
                .body(List.of(repository, repository1));

        when(webClientMock
                .get()
                .uri(eq(uri), eq(userName))
                .retrieve()
                .onStatus(any(), any())
                .toEntityList(Repository.class)
        ).thenReturn(Mono.just(responseEntity));

        String baseUrl = "https://api.github.com";
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class, RETURNS_DEEP_STUBS);
        when(webClientBuilder.baseUrl(baseUrl).build()).thenReturn(webClientMock);
        GithubClient githubClient = new GithubClient(webClientBuilder, baseUrl);

        Mono<List<Repository>> result = githubClient.fetchRepositories(userName);

        Set<String> repositoryNames = Set.of("repo", "repo1");
        StepVerifier.create(result)
                .expectNextMatches(repos -> {
                    Set<String> actualRepoNames = repos.stream().map(Repository::getName).collect(Collectors.toSet());
                    return repositoryNames.containsAll(actualRepoNames);
                })
                .verifyComplete();
    }

    @Test
    void whenUserWithProvidedNameDoesNotExistsShouldRespondWith404Status() {
        String userName = "NonExistentUser";
        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);

        when(webClientMock
                .get()
                .uri(eq("/users/{userName}/repos"), eq(userName))
                .retrieve()
                .onStatus(any(), any())
                .toEntityList(Repository.class)
        ).thenReturn(Mono.error(() -> new NotFoundException("404 Not Found")));

        String baseUrl = "https://api.github.com";
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class, RETURNS_DEEP_STUBS);
        when(webClientBuilder.baseUrl(baseUrl).build()).thenReturn(webClientMock);
        GithubClient githubClient = new GithubClient(webClientBuilder, baseUrl);

        Mono<List<Repository>> result = githubClient.fetchRepositories(userName);

        StepVerifier.create(result)
                .verifyError(NotFoundException.class);
    }

    @Test
    void whenUserAndRepositoryExistsShouldFetchBranchesSuccessfully() {
        Repository repository = new Repository();
        repository.setOwner(new Owner("user", 1L, ""));
        repository.setName("repo");

        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);

        Branch branch1 = new Branch("main", new Commit(""));
        Branch branch2 = new Branch("feature-branch", new Commit(""));
        ResponseEntity<List<Branch>> responseEntity = ResponseEntity.ok()
                .body(List.of(branch1, branch2));

        when(webClientMock
                .get()
                .uri("/repos/{userName}/{repositoryName}/branches", "user", "repo")
                .retrieve()
                .onStatus(any(), any())
                .toEntityList(Branch.class)
        ).thenReturn(Mono.just(responseEntity));

        String baseUrl = "https://api.github.com";
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class, RETURNS_DEEP_STUBS);
        when(webClientBuilder.baseUrl(baseUrl).build()).thenReturn(webClientMock);
        GithubClient githubClient = new GithubClient(webClientBuilder, baseUrl);

        Mono<Repository> result = githubClient.fetchBranchesFor(repository);

        StepVerifier.create(result)
                .expectNextMatches(repo -> repo.getBranches().size() == 2)
                .verifyComplete();
    }

    @Test
    void whenRepositoryNotExistsShouldRespondWith404Status() {
        Repository repository = new Repository();
        repository.setOwner(new Owner("user", 1L, ""));
        repository.setName("nonExistentRepo");

        WebClient webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);

        when(webClientMock
                .get()
                .uri("/repos/{userName}/{repositoryName}/branches", "user", "nonExistentRepo")
                .retrieve()
                .onStatus(any(), any())
                .toEntityList(Branch.class)
        ).thenReturn(Mono.error(() -> new NotFoundException("404 Not Found")));

        String baseUrl = "https://api.github.com";
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class, RETURNS_DEEP_STUBS);
        when(webClientBuilder.baseUrl(baseUrl).build()).thenReturn(webClientMock);
        GithubClient githubClient = new GithubClient(webClientBuilder, baseUrl);

        Mono<Repository> result = githubClient.fetchBranchesFor(repository);

        StepVerifier.create(result)
                .verifyError(NotFoundException.class);
    }
}