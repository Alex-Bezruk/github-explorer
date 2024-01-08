package com.bezruk.github.explorer.client;

import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GithubClient {

    public List<Repository> fetchRepositories(String userName) {
        return Collections.emptyList();
    }

    public Map<Long, List<Branch>> fetchBranches(List<Long> repositoryIds) {
        return Collections.emptyMap();
    }

}
