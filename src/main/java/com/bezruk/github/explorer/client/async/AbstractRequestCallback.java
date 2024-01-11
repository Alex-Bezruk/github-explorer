package com.bezruk.github.explorer.client.async;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestCallback<ID> implements AsyncReqeustCallback<ID> {

    @Override
    public void logRequestFailure(String url) {
        String message = String.format("Fetch of Branches for Repository failed. Request URL: %s", url);
        log.debug(message);
    }

    @Override
    public void logResponseWithErrorCode(String url, int code) {
        String message = String.format(
                "Fetch of Branches for Repository failed." +
                        " Github Api Responded with: %d. Request URL: %s", code, url);
        log.debug(message);
    }
}
