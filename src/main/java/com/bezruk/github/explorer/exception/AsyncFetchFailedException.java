package com.bezruk.github.explorer.exception;

public class AsyncFetchFailedException extends ApplicationException {
    public AsyncFetchFailedException(String message) {
        super(500, message);
    }
}
