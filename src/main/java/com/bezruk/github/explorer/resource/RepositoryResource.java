package com.bezruk.github.explorer.resource;

import com.bezruk.github.explorer.exception.BadRequestException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

@Path("/repositories")
public class RepositoryResource {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepositories(@QueryParam("userName") String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new BadRequestException("userName parameter is required");
        }

        return Response.ok(Collections.emptyList()).build();
    }
}
