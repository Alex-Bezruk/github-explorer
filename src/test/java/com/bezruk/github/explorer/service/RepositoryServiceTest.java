package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.model.Repository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
public class RepositoryServiceTest {

    @Inject
    RepositoryService repositoryService;

    @InjectMock
    GithubClient githubClient;

    @Test
    void testGetRepositories() {
        String userName = "testUser";
        List<Repository> mockRepositories = Collections.singletonList(new Repository());

        when(githubClient.fetchRepositories(userName)).thenReturn(mockRepositories);

        List<Repository> result = repositoryService.getRepositories(userName);

        assertEquals(mockRepositories, result);
    }
}
