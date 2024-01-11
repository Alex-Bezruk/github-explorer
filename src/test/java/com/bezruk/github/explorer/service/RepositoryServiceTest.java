package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Commit;
import com.bezruk.github.explorer.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RepositoryServiceTest {

    @Mock
    GithubClient githubClient;

    @Mock
    BranchService branchService;

    @InjectMocks
    RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRepositories() {
        String userName = "testUser";
        Repository repository = new Repository();
        repository.setName("test");
        List<Repository> mockRepositories = Collections.singletonList(repository);

        when(githubClient.fetchRepositories(userName)).thenReturn(mockRepositories);

        Branch branch = new Branch("branch1", new Commit("sha1"));
        Branch branch2 = new Branch("branch2", new Commit("sha2"));
        Branch branch3 = new Branch("branch3", new Commit("sha3"));
        Branch branch4 = new Branch("branch4", new Commit("sha4"));
        List<Branch> branchList1 = List.of(branch, branch2);
        List<Branch> branchList2 = List.of(branch3, branch4);

        when(branchService.getBranches(userName, List.of("test")))
                .thenReturn(Map.of("repo1", branchList1, "repo2", branchList2));

        List<Repository> result = repositoryService.getRepositories(userName);

        assertEquals(mockRepositories, result);
    }
}
