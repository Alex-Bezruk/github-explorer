package com.bezruk.github.explorer.model;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ApplicationError {

    @JsonAlias("Message")
    private String message;
    private Integer status;

    private ApplicationError() {}

    public static ApplicationError fromException(ApplicationException exception) {
        ApplicationError error = new ApplicationError();
        error.setMessage(exception.getMessage());
        error.setStatus(exception.getStatus());
        return error;
    }
}
