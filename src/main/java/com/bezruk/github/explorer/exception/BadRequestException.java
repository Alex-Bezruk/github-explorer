package com.bezruk.github.explorer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class BadRequestException extends ApplicationException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
