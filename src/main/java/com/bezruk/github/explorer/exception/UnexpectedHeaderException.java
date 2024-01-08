package com.bezruk.github.explorer.exception;

import lombok.Getter;

@Getter
public class UnexpectedHeaderException extends ApplicationException {
    public UnexpectedHeaderException(String message) {
        super(406, message);
    }
}
