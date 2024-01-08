package com.bezruk.github.explorer.model;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationError {

    @JsonAlias("Message")
    private String message;
    private Integer status;

    private ApplicationError() {}

    public static ApplicationError fromException(ApplicationException exception) {
        return ApplicationError.builder()
                .message(exception.getMessage())
                .status(exception.getStatus())
                .build();
    }
}
