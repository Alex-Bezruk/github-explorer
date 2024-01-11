package com.bezruk.github.explorer.callback;

import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.client.async.AbstractRequestCallback;
import com.bezruk.github.explorer.model.Branch;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Builder
@Getter
public class OnFetchBranchesCallback extends AbstractRequestCallback<String> {

    private final String parentRecordId;
    private final CountDownLatch callbacksCounter;
    private final Map<String, List<Branch>> resultStore;
    private final ApplicationObjectMapper mapper;

    @Override
    public void onFailure(Call call, @NonNull IOException e) {
        callbacksCounter.countDown();
        String requestUrl = call.request().url().toString();
        logRequestFailure(requestUrl);
    }

    @Override
    public void onResponse(@NonNull Call call, Response response) {
        if (response.isSuccessful()) {
            var typeReference = new TypeReference<List<Branch>>() {};
            var branches = mapper.readResponse(response, typeReference);
            resultStore.put(parentRecordId, branches);
            callbacksCounter.countDown();
        } else {
            callbacksCounter.countDown();
            String requestUrl = response.request().url().toString();
            logResponseWithErrorCode(requestUrl, response.code());
        }
    }
}
