package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Commit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BranchServiceTest {

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private BranchService branchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBranches_shouldReturnMappedBranches() {
        String userName = "testUser";
        List<String> repositoryNames = Arrays.asList("repo1", "repo2");

        Branch branch = new Branch("branch1", new Commit("sha1"));
        Branch branch2 = new Branch("branch2", new Commit("sha2"));
        Branch branch3 = new Branch("branch3", new Commit("sha3"));
        Branch branch4 = new Branch("branch4", new Commit("sha4"));
        List<Branch> branchList1 = List.of(branch, branch2);
        List<Branch> branchList2 = List.of(branch3, branch4);

        when(githubClient.fetchBranches(userName, repositoryNames))
                .thenReturn(Map.of("repo1", branchList1, "repo2", branchList2));

        Map<String, List<Branch>> result = branchService.getBranches(userName, repositoryNames);

        assertEquals(branchList1, result.get("repo1"));
        assertEquals(branchList2, result.get("repo2"));
    }
}
