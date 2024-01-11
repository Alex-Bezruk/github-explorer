package com.bezruk.github.explorer.client.async;

import okhttp3.Callback;

public interface AsyncReqeustCallback<ID> extends Callback {

    ID getParentRecordId();

    void logRequestFailure(String url);

    void logResponseWithErrorCode(String url, int code);
}
