package com.bezruk.github.explorer.resources;

import com.bezruk.github.explorer.model.Repository;
import com.bezruk.github.explorer.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
public class RepositoryResource {

    private final RepositoryService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Repository> getRepositories(@RequestParam(required = false) String userName) {
        return service.getRepositories(userName);
    }
}
