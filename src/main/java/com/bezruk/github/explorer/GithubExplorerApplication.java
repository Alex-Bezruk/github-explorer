package com.bezruk.github.explorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class GithubExplorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubExplorerApplication.class, args);
    }

}
