package com.bezruk.github.explorer.client;

import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GithubClientTest {

    @Inject
    GithubClient client;

    @Test
    void fetchRepositories() {
        List<Repository> repositories = client.fetchRepositories("Alex-Bezruk");

        assertFalse(repositories.isEmpty());
    }

    @Test
    void fetchBranches() {
        Map<Long, List<Branch>> repositoryBranches = client.fetchBranches(List.of(1L, 2L, 3L));

        assertEquals(3, repositoryBranches.size());
        repositoryBranches.values()
                .stream()
                .flatMap(List::stream)
                .forEach(branch -> assertNotNull(branch.getName()));
    }
}