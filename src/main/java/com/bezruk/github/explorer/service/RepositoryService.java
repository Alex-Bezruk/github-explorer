package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
public class RepositoryService {

    private final GithubClient githubClient;
    private final BranchService branchService;

    public List<Repository> getRepositories(String userName) {
        List<Repository> repositories = githubClient.fetchRepositories(userName);

        List<String> repositoryNames = repositories.stream().map(Repository::getName).toList();
        Map<String, List<Branch>> repositoryToBranches = branchService.getBranches(userName, repositoryNames);

        return repositories.stream()
                .map(repository -> toEnhancedWithBranches(repository, repositoryToBranches))
                .toList();
    }

    private Repository toEnhancedWithBranches(Repository repository, Map<String, List<Branch>> idToBranches) {
        List<Branch> branches = idToBranches.get(repository.getName());
        repository.setBranches(branches);
        return repository;
    }
}
