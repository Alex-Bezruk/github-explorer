package com.bezruk.github.explorer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApplicationException extends RuntimeException {

    private final Integer status;

    public ApplicationException(Integer status, String message) {
        super(message);
        this.status = status;
    }
}
