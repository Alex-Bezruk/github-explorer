package com.bezruk.github.explorer.callback;

import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.model.Branch;
import com.bezruk.github.explorer.model.Commit;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Call;
import okhttp3.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OnFetchBranchesCallbackTest {

    private final Call call = mock(Call.class, RETURNS_DEEP_STUBS);
    private final Response successResponse = mock(Response.class);
    private final Response failureResponse = mock(Response.class, RETURNS_DEEP_STUBS);
    private final ApplicationObjectMapper mapper = mock(ApplicationObjectMapper.class);


    private OnFetchBranchesCallback callback;

    @BeforeEach
    public void initializeCallbackInstance() {
        var store = new ConcurrentHashMap<String, List<Branch>>();
        callback = new OnFetchBranchesCallback("test", new CountDownLatch(1), store, mapper);
        callback = spy(callback);
    }

    @Test
    void onFailure_shouldDecrementCallbacksCounterAndLogRequestFailure() {
        when(call.request().url().toString()).thenReturn(RandomStringUtils.random(15));

        callback.onFailure(call, new IOException("Test exception"));

        assertEquals(0, callback.getCallbacksCounter().getCount());
        verify(callback).logRequestFailure(anyString());
    }

    @Test
    void onResponse_shouldProcessSuccessfulResponseAndDecrementCallbacksCounter() {
        when(successResponse.isSuccessful()).thenReturn(true);

        Branch testBranch1 = new Branch("Branch1", new Commit("sha1"));
        Branch testBranch2 = new Branch("Branch2", new Commit("sha2"));
        List<Branch> branches = List.of(testBranch1, testBranch2);
        when(mapper.readResponse(eq(successResponse), any(TypeReference.class))).thenReturn(branches);

        callback.onResponse(call, successResponse);

        assertEquals(0, callback.getCallbacksCounter().getCount());
        assertEquals(branches, callback.getResultStore().get(callback.getParentRecordId()));
    }

    @Test
    void onResponse_shouldProcessUnsuccessfulResponseAndDecrementCallbacksCounter() {
        when(failureResponse.request().url().toString()).thenReturn(RandomStringUtils.random(15));
        when(failureResponse.isSuccessful()).thenReturn(false);
        when(failureResponse.code()).thenReturn(404);

        callback.onResponse(call, failureResponse);

        assertEquals(0, callback.getCallbacksCounter().getCount());
        verify(callback).logResponseWithErrorCode(anyString(), eq(404));
    }
}
