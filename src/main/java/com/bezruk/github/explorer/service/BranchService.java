package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.model.Branch;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class BranchService {

    private final GithubClient githubClient;

    public Map<String, List<Branch>> getBranches(String userName, List<String> repositoryNames) {
        return githubClient.fetchBranches(userName, repositoryNames);
    }
}
