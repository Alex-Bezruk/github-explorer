package com.bezruk.github.explorer.exception;

import lombok.Getter;

@Getter
public class UnsupportedMediaTypeException extends ApplicationException {
    public UnsupportedMediaTypeException(String message) {
        super(406, message);
    }
}
