package com.bezruk.github.explorer.exception;

import java.io.IOException;

public class InternalErrorException extends ApplicationException{
    private InternalErrorException(String message) {
        super(500, message);
    }

    public static InternalErrorException fromParent(Exception e, String message) {
        String decoratedMessage =
                String.format("Unexpected Internal Error. %s. Parent Exception: %s", message, e.getMessage());
        return new InternalErrorException(decoratedMessage);
    }

    public static InternalErrorException asErrorDuringResponseRead(IOException e) {
        String decoratedMessage =
                String.format("Error during reading of Response from Github. Parent Exception: %s", e.getMessage());
        return new InternalErrorException(decoratedMessage);
    }
}
