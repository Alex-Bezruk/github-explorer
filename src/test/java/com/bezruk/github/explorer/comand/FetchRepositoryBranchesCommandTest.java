package com.bezruk.github.explorer.comand;

import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.callback.OnFetchBranchesCallback;
import com.bezruk.github.explorer.client.async.AsyncHttpCall;
import com.bezruk.github.explorer.exception.AsyncFetchFailedException;
import com.bezruk.github.explorer.exception.InternalErrorException;
import com.bezruk.github.explorer.model.Branch;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FetchRepositoryBranchesCommandTest {

    private FetchChildRecordsCommand<String, List<Branch>> command;

    private List<AsyncHttpCall<String>> mockedCalls;

    @BeforeEach
    void buildCommand() {
        var mapper = new ApplicationObjectMapper(new ObjectMapper());
        var repositoryNames = List.of("repo1", "repo2");
        List<AsyncHttpCall<String>> asyncCalls = repositoryNames.stream()
                .map(repo -> new AsyncHttpCall<>(repo, mock(Call.class, RETURNS_DEEP_STUBS)))
                .toList();
        mockedCalls = asyncCalls;
        command = new FetchRepositoryBranchesCommand(mapper, asyncCalls);
    }

    @Test
    void execute_Success() {
        mockedCalls.forEach(this::mockSuccessfulCallResponse);

        Map<String, List<Branch>> repositoryBranches = command.execute();

        assertEquals(Set.of("repo1", "repo2"), repositoryBranches.keySet());
        assertEquals(2, repositoryBranches.get("repo1").size());
        assertEquals(2, repositoryBranches.get("repo2").size());
    }

    @Test
    void execute_shouldValidateExecutionResult() {
        mockedCalls.forEach(this::mockFailedCallbackInvocation);

        try {
            command.execute();
        } catch (AsyncFetchFailedException e) {
            assertEquals(e.getMessage(), command.getErrorMessage());
        }
    }

    @Test
    void execute_shouldThrowExecutionOnThreadInterruption() {
        mockedCalls.forEach(this::mockFailedCallbackThreadInterruption);
        assertThrows(InternalErrorException.class, () -> command.execute());
    }

    @SneakyThrows
    private void mockSuccessfulCallResponse(AsyncHttpCall<String> call) {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        String body = String.format("[{\"name\":\"%s\"}, {\"name\":\"%s\"}]", "branch1", "branch2");
        when(response.body().string()).thenReturn(body);
        when(response.isSuccessful()).thenReturn(true);

        mockSuccessfulCallbackInvocation(call, response);
    }

    private void mockSuccessfulCallbackInvocation(AsyncHttpCall<String> call, Response response) {
        doAnswer(invocationOnMock -> {
            OnFetchBranchesCallback callback = invocationOnMock.getArgument(0);
            callback.onResponse(call.asyncCall(), response);
            return null;
        }).when(call.asyncCall()).enqueue(any());
    }

    private void mockFailedCallbackInvocation(AsyncHttpCall<String> call) {
        when(call.asyncCall().request().url().toString()).thenReturn("/test-url");
        doAnswer(invocationOnMock -> {
            OnFetchBranchesCallback callback = invocationOnMock.getArgument(0);
            callback.onFailure(call.asyncCall(), new IOException("Unable to read response"));
            return null;
        }).when(call.asyncCall()).enqueue(any());
    }

    private void mockFailedCallbackThreadInterruption(AsyncHttpCall<String> call) {
        when(call.asyncCall().request().url().toString()).thenReturn("/test-url");
        doAnswer(invocationOnMock -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(call.asyncCall()).enqueue(any());
    }

}