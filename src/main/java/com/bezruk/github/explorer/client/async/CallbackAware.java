package com.bezruk.github.explorer.client.async;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface CallbackAware<ID, T> {

    AsyncReqeustCallback<ID> buildCallback(
            ID parentRecordId,
            Map<ID, T> resultStore,
            CountDownLatch callbacksCounter);
}
