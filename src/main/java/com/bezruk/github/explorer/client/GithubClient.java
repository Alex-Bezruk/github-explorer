package com.bezruk.github.explorer.client;

import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.client.async.AsyncHttpCall;
import com.bezruk.github.explorer.comand.FetchRepositoryBranchesCommand;
import com.bezruk.github.explorer.exception.GithubRequestException;
import com.bezruk.github.explorer.exception.InternalErrorException;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GithubClient {


    private final OkHttpClient httpClient;
    private final String apiUrl;
    private final ApplicationObjectMapper mapper;

    @Inject
    public GithubClient(OkHttpClient httpClient,
                        @ConfigProperty(name = "github.api.url") String apiUrl,
                        ApplicationObjectMapper mapper) {
        this.httpClient = httpClient;
        this.apiUrl = apiUrl;
        this.mapper = mapper;
    }

    public List<Repository> fetchRepositories(String userName) {
        String uri = String.format("/users/%s/repos", userName);
        TypeReference<List<Repository>> listOfRepositoriesType = new TypeReference<>() {};
        return getList(uri, listOfRepositoriesType);
    }

    public Map<String, List<Branch>> fetchBranches(String userName, List<String> repositoryNames) {
        List<AsyncHttpCall<String>> fetchBranchesCalls = repositoryNames.stream()
                .map(repositoryName -> buildFetchBranchesCall(userName, repositoryName))
                .toList();

        return new FetchRepositoryBranchesCommand(mapper, fetchBranchesCalls).execute();
    }

    private <T> T getList(String uri, TypeReference<T> typeReference) {
        String url = apiUrl + uri;
        Request request = buildGetRequest(url);
        Response response = executeRequest(request);
        validateResponse(response);
        return mapper.readResponse(response, typeReference);
    }

    private Request buildGetRequest(String url) {
        try {
            return new Request.Builder().url(url).build();
        } catch (IllegalArgumentException e) {
            String message = String.format("Unable to create Request. Request URL is incorrect. URL: %s", url);
            throw InternalErrorException.fromParent(e, message);
        }
    }

    private void validateResponse(Response response) {
        if (!response.isSuccessful()) {
            var requestUrl = response.request().url().toString();
            throw new GithubRequestException(response.code(), requestUrl);
        }
    }

    private Response executeRequest(Request request) {
        try {
            return httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw InternalErrorException.asErrorDuringResponseRead(e);
        }
    }

    private AsyncHttpCall<String> buildFetchBranchesCall(String userName, String repositoryName) {
        String url = String.format("%s/repos/%s/%s/branches", apiUrl, userName, repositoryName);
        Request request = buildGetRequest(url);
        return new AsyncHttpCall<>(repositoryName, httpClient.newCall(request));
    }
}
