package com.bezruk.github.explorer.application;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.model.ApplicationError;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<ApplicationException> {
    @Override
    public Response toResponse(ApplicationException exception) {
        ApplicationError error = ApplicationError.fromException(exception);
        return Response.status(error.getStatus())
                .entity(error)
                .build();
    }
}
