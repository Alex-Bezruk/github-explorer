package com.bezruk.github.explorer.resources;

import com.bezruk.github.explorer.exception.NotFoundException;
import com.bezruk.github.explorer.model.Repository;
import com.bezruk.github.explorer.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@WebFluxTest(RepositoryResource.class)
public class RepositoryResourceTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RepositoryService repositoryService;

    @Test
    public void whenServiceReturnsRepositoriesShouldRespondWithSuccess() {

        String userName = "Alex-Bezruk-Bezruk-333";
        Repository repository = new Repository();
        Repository repository1 = new Repository();
        repository.setName("repo");
        repository1.setName("repo1");

        Mockito.when(repositoryService.getRepositories(userName))
                .thenReturn(Flux.just(repository, repository1));

        webTestClient.get()
                .uri("/repositories?userName={userName}", userName)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Repository.class)
                .contains(repository, repository1);
    }

    @Test
    public void whenUserNotExistsShouldRespondWith404NotFound() {
        String userName = "Alex-Bezruk-Bezruk-333";

        Mockito.when(repositoryService.getRepositories(userName))
                .thenReturn(Flux.error(new NotFoundException("404 NOT FOUND")));

        webTestClient.get()
                .uri("/repositories?userName={userName}", userName)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void whenNotCorrectAcceptHeaderProvidedShouldRespondWith406NotAcceptable() {
        String userName = "Alex-Bezruk-Bezruk-333";

        webTestClient.get()
                .uri("/repositories?userName={userName}", userName)
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
