package com.bezruk.github.explorer.service;

import com.bezruk.github.explorer.client.GithubClient;
import com.bezruk.github.explorer.exception.BadRequestException;
import com.bezruk.github.explorer.model.Repository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final GithubClient githubClient;

    public Flux<Repository> getRepositories(String userName) {
        validateUserName(userName);

        return githubClient.fetchRepositories(userName)
                .flatMapMany(Flux::fromIterable)
                .filter(not(Repository::getFork))
                .flatMap(githubClient::fetchBranchesFor);
    }

    private void validateUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            throw new BadRequestException("Required parameter missed: userName");
        }
    }
}
