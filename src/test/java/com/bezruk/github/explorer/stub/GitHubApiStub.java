package com.bezruk.github.explorer.stub;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GitHubApiStub {

    private final WireMockServer wireMockServer = new WireMockServer(8084);

    public void startWireMock() {
        wireMockServer.start();
    }

    public void stopWireMock() {
        wireMockServer.stop();
    }

    public void stubRepositoriesEndpoint(String userName, List<String> repositories) {
        String body = String.format("[{\"name\":\"%s\"}, {\"name\":\"%s\"}]", repositories.get(0), repositories.get(1));

        String url = String.format("/users/%s/repos", userName);
        wireMockServer.stubFor(WireMock.get(urlEqualTo(url))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
    }

    public void stubBranchesEndpoint(String userName, List<String> repositories) {
        repositories.forEach(repo -> {
            String path = String.format("/repos/%s/%s/branches", userName, repo);
            wireMockServer.stubFor(WireMock.get(urlPathMatching(path))
                    .willReturn(WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("[{\"name\":\"branch1\"}, {\"name\":\"branch2\"}]")));
        });
    }
}