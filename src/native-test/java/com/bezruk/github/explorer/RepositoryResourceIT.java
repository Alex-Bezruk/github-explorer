package com.bezruk.github.explorer;

import com.bezruk.github.explorer.resource.RepositoryResource;
import com.bezruk.github.explorer.service.RepositoryService;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class RepositoryResourceIT extends RepositoryResource {
    public RepositoryResourceIT(RepositoryService service) {
        super(service);
    }


}
