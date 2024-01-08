package com.bezruk.github.explorer.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends ApplicationException{
    public NotFoundException(String message) {
        super(404, message);
    }
}
