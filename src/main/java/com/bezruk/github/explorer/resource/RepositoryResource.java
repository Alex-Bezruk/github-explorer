package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.exception.BadRequestException;
import com.bezruk.github.explorer.service.RepositoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/repositories")
public class RepositoryResource {

    private final RepositoryService service;

    @Inject
    public RepositoryResource(RepositoryService service) {
        this.service = service;
    }
    @GET
    public Response getRepositories(@QueryParam("userName") String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new BadRequestException("userName parameter is required");
        }

        return Response.ok(service.getRepositories(userName)).build();
    }
}
