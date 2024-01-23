package com.bezruk.github.explorer.client;

import com.bezruk.github.explorer.exception.NotFoundException;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.GithubError;
import com.bezruk.github.explorer.model.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.function.Predicate.isEqual;

@Component
public class GithubClient {

    private final WebClient webClient;

    public GithubClient(WebClient.Builder webClientBuilder, @Value("${github.api.url}") String githubHost) {
        this.webClient = webClientBuilder.baseUrl(githubHost).build();
    }

    public Mono<List<Repository>> fetchRepositories(String userName) {
        String uri = "/users/{userName}/repos";
        return webClient.get()
                .uri(uri, userName)
                .retrieve()
                .onStatus(isEqual(HttpStatus.NOT_FOUND), this::handleNotFoundResponse)
                .toEntityList(Repository.class)
                .mapNotNull(HttpEntity::getBody);
    }

    public Mono<Repository> fetchBranchesFor(Repository repository) {
        String userName = repository.getOwner().login();
        String repositoryName = repository.getName();
        return webClient.get()
                .uri("/repos/{userName}/{repositoryName}/branches", userName, repositoryName)
                .retrieve()
                .onStatus(isEqual(HttpStatus.NOT_FOUND), this::handleNotFoundResponse)
                .toEntityList(Branch.class)
                .mapNotNull(HttpEntity::getBody)
                .map(repository::withBranches);
    }

    private Mono<NotFoundException> handleNotFoundResponse(ClientResponse response) {
        return response.toEntity(GithubError.class)
                .mapNotNull(HttpEntity::getBody)
                .map(this::getNotFoundMessage)
                .map(NotFoundException::new);
    }

    private String getNotFoundMessage(GithubError error) {
        return String.format("GithubAPI responded with status 404. Original Message: %s.", error.getMessage());
    }
}
