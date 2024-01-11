package com.bezruk.github.explorer.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends ApplicationException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
