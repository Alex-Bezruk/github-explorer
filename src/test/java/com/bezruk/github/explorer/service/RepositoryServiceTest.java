package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.exception.BadRequestException;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Commit;
import com.bezruk.github.explorer.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RepositoryServiceTest {

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private RepositoryService repositoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenUserNameIsPresentShouldSuccessfullyLoadRepositoriesWithBranches() {
        String userName = "testUser";

        Repository repo1 = new Repository();
        repo1.setName("repo1");
        repo1.setFork(false);

        Repository repo2 = new Repository();
        repo2.setName("repo2");
        repo2.setFork(false);

        Branch branch = new Branch("master", new Commit(""));
        Branch branch1 = new Branch("feature", new Commit(""));
        Branch branch2 = new Branch("feature", new Commit(""));


        repo1.withBranches(List.of(branch, branch1));
        repo2.withBranches(List.of(branch2));

        when(githubClient.fetchRepositories(userName)).thenReturn(Mono.just(List.of(repo1, repo2)));
        when(githubClient.fetchBranchesFor(repo1)).thenReturn(Mono.just(repo1));
        when(githubClient.fetchBranchesFor(repo2)).thenReturn(Mono.just(repo2));

        Flux<Repository> result = repositoryService.getRepositories(userName);

        StepVerifier.create(result)
                .expectNextMatches(repo -> repo.getName().equals("repo1") && !repo.getFork() && repo.getBranches().size() == 2)
                .expectNextMatches(repo -> repo.getName().equals("repo2") && !repo.getFork() && repo.getBranches().size() == 1)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenUserNameIsEmpty() {
        String userName = "";

        assertThrows(BadRequestException.class, () -> repositoryService.getRepositories(userName));
    }

    @Test
    public void shouldThrowBadRequestExceptionWhenUserNameIsNull() {
        String userName = null;

        assertThrows(BadRequestException.class, () -> repositoryService.getRepositories(userName));
    }
}
