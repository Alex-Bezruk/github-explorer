package com.bezruk.github.explorer.comand;

import com.bezruk.github.explorer.client.async.AsyncHttpCall;
import com.bezruk.github.explorer.client.async.CallbackAware;
import com.bezruk.github.explorer.exception.AsyncFetchFailedException;
import com.bezruk.github.explorer.exception.InternalErrorException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public abstract class FetchChildRecordsCommand<ID, T> implements CallbackAware<ID, T> {
    private final List<AsyncHttpCall<ID>> calls;
    private final Map<ID, T> resultStore;

    private final CountDownLatch callbackCounter;

    protected abstract String getErrorMessage();

    public FetchChildRecordsCommand(List<AsyncHttpCall<ID>> calls) {
        this.calls = calls;
        this.resultStore = new ConcurrentHashMap<>();
        this.callbackCounter = new CountDownLatch(calls.size());
    }

    public Map<ID, T> execute() {
        calls.stream().forEach(call -> {
            var callback = buildCallback(call.parentRecordId(), resultStore, callbackCounter);
            call.asyncCall().enqueue(callback);
        });
        freezeExecutionWithLatch();
        validateResult();
        return resultStore;
    }

    private void validateResult() {
        if (resultStore.size() != calls.size()) {
            throw new AsyncFetchFailedException(getErrorMessage());
        }
    }

    private void freezeExecutionWithLatch() {
        try {
            callbackCounter.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            String message =
                    "One of requests to Github /branches endpoint failed due to timeout or child thead was interrupted";
            throw InternalErrorException.fromParent(e, message);
        }
    }
}
