package com.bezruk.github.explorer.application;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.model.ApplicationError;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class ExceptionHandler implements ExceptionMapper<ApplicationException> {

    @Override
    public Response toResponse(ApplicationException exception) {
        ApplicationError error = ApplicationError.fromException(exception);
        return Response.status(error.getStatus())
                .header("content-type", MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
