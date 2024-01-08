package com.bezruk.github.explorer.interceptor;

import com.bezruk.github.explorer.exception.UnexpectedHeaderException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;

@Provider
public class RepositoryResourceInterceptor implements ContainerRequestFilter {

    private static final List<String> acceptableMediaTypes = List.of(APPLICATION_JSON, MEDIA_TYPE_WILDCARD);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        boolean isRepositoryRequest = requestContext.getUriInfo().getPath().contains("repositories");
        if (!isRepositoryRequest) {
            return;
        }

        validateRequestMediaType(requestContext);
    }

    private void validateRequestMediaType(ContainerRequestContext requestContext) {
        String acceptHeader = requestContext.getHeaders().getFirst("Accept") != null ?
                requestContext.getHeaders().getFirst("Accept") : APPLICATION_JSON;

        if (!acceptableMediaTypes.contains(acceptHeader)) {
            throw new UnexpectedHeaderException("Unsupported media type: " + acceptHeader);
        }
    }
}
