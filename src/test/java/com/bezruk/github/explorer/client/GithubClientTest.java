package com.bezruk.github.explorer.client;


import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.callback.OnFetchBranchesCallback;
import com.bezruk.github.explorer.exception.AsyncFetchFailedException;
import com.bezruk.github.explorer.exception.GithubRequestException;
import com.bezruk.github.explorer.exception.InternalErrorException;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GithubClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OkHttpClient httpClient;

    @InjectMocks
    private GithubClient githubClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String testUrl = String.format("https://%s", RandomStringUtils.randomAlphabetic(10));
        githubClient = new GithubClient(httpClient, testUrl, new ApplicationObjectMapper(new ObjectMapper()));
    }

    @SneakyThrows
    private void mockSuccessfulRepositoriesResponse(Response response) {
        String randomNumeric = RandomStringUtils.randomNumeric(15);
        String anotherRandomNumeric = RandomStringUtils.randomNumeric(15);
        String json = String.format("[{\"name\":\"%s\"}, {\"name\":\"%s\"}]", randomNumeric, anotherRandomNumeric);
        when(response.body().string()).thenReturn(json);
        when(response.isSuccessful()).thenReturn(true);
    }

    @SneakyThrows
    private void mockSuccessfulBranchesResponse(Response response) {
        String randomNumeric = RandomStringUtils.randomNumeric(15);
        String anotherRandomNumeric = RandomStringUtils.randomNumeric(15);
        String json = String.format("[{\"name\":\"%s\"}, {\"name\":\"%s\"}]", randomNumeric, anotherRandomNumeric);
        when(response.body().string()).thenReturn(json);
        when(response.isSuccessful()).thenReturn(true);
    }

    @Test
    void fetchRepositories_shouldFetchRepositoriesSuccessfully() throws IOException {
        String userName = "testUser";

        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        mockSuccessfulRepositoriesResponse(response);
        when(httpClient.newCall(any(Request.class)).execute()).thenReturn(response);

        List<Repository> actualRepositories = githubClient.fetchRepositories(userName);
        assertEquals(2, actualRepositories.size());
        actualRepositories.forEach(repository -> assertNotNull(repository.getId()));
    }

    @Test
    void fetchRepositories_shouldThrowExceptionOnUnsuccessfulResponse() throws IOException {
        String userName = "testUser";

        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(httpClient.newCall(any(Request.class)).execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.request()).thenReturn(new Request.Builder().url("https://api.github.com").build());

        assertThrows(GithubRequestException.class, () -> githubClient.fetchRepositories(userName));
    }

    @Test
    void fetchRepositories_shouldThrowExceptionOnNotValidUrl() {
        String userName = "testUser";

        githubClient = new GithubClient(httpClient, "wrong_url", new ApplicationObjectMapper(new ObjectMapper()));
        assertThrows(InternalErrorException.class, () -> githubClient.fetchRepositories(userName));
    }

    @Test
    void fetchBranches_shouldFetchBranchesSuccessFully() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        mockSuccessfulBranchesResponse(response);

        Call mockedCall = mock(Call.class);
        when(mockedCall.execute()).thenReturn(response);
        when(httpClient.newCall(any(Request.class))).thenReturn(mockedCall);
        doAnswer(invocationOnMock -> {
            OnFetchBranchesCallback callback = invocationOnMock.getArgument(0);
            callback.getCallbacksCounter().countDown();
            callback.getResultStore().put(callback.getParentRecordId(), Collections.emptyList());
            return null;
        }).when(mockedCall).enqueue(any());

        String userName = "testUser";
        Map<String, List<Branch>> branches = githubClient.fetchBranches(userName, List.of("Repo1", "Repo2"));

        assertEquals(branches.keySet(), Set.of("Repo1", "Repo2"));
    }

    @Test
    void fetchBranches_shouldThrowExceptionBranchesRequestFailure() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        mockSuccessfulBranchesResponse(response);

        Call mockedCall = mock(Call.class);
        when(mockedCall.execute()).thenReturn(response);
        when(httpClient.newCall(any(Request.class))).thenReturn(mockedCall);
        doAnswer(invocationOnMock -> {
            OnFetchBranchesCallback callback = invocationOnMock.getArgument(0);
            callback.getCallbacksCounter().countDown();
            return null;
        }).when(mockedCall).enqueue(any());

        String userName = "testUser";

        assertThrows(AsyncFetchFailedException.class,
                () -> githubClient.fetchBranches(userName, List.of("Repo1", "Repo2")));
    }

    @Test
    void fetchBranches_shouldThrowExceptionBranchesThreadInterruption() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        mockSuccessfulBranchesResponse(response);

        Call mockedCall = mock(Call.class);
        when(mockedCall.execute()).thenReturn(response);
        when(httpClient.newCall(any(Request.class))).thenReturn(mockedCall);
        doAnswer(invocationOnMock -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(mockedCall).enqueue(any());

        String userName = "testUser";

        assertThrows(InternalErrorException.class,
                () -> githubClient.fetchBranches(userName, List.of("Repo1", "Repo2")));
    }
}
