package com.bezruk.github.explorer.comand;

import com.bezruk.github.explorer.application.ApplicationObjectMapper;
import com.bezruk.github.explorer.callback.OnFetchBranchesCallback;
import com.bezruk.github.explorer.client.async.AsyncHttpCall;
import com.bezruk.github.explorer.client.async.AsyncReqeustCallback;
import com.bezruk.github.explorer.model.Branch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FetchRepositoryBranchesCommand extends FetchChildRecordsCommand<String, List<Branch>> {

    private final ApplicationObjectMapper mapper;

    public FetchRepositoryBranchesCommand(ApplicationObjectMapper mapper, List<AsyncHttpCall<String>> asyncCalls) {
        super(asyncCalls);
        this.mapper = mapper;
    }

    @Override
    public AsyncReqeustCallback<String> buildCallback(String parentRecordId,
                                                      Map<String, List<Branch>> resultStore,
                                                      CountDownLatch callbacksCounter) {

        return OnFetchBranchesCallback.builder()
                .parentRecordId(parentRecordId)
                .mapper(mapper)
                .resultStore(resultStore)
                .callbacksCounter(callbacksCounter)
                .build();
    }

    @Override
    protected String getErrorMessage() {
        return "Unexpected error during preloading of branches for Repository.";
    }
}
