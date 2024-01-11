package com.bezruk.github.explorer.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import okhttp3.OkHttpClient;


@ApplicationScoped
public class HttpClientConfiguration {

    @Produces
    @ApplicationScoped
    public OkHttpClient httpClient() {
        return new OkHttpClient();
    }

    @Produces
    @ApplicationScoped
    public ApplicationObjectMapper applicationObjectMapper() {
        return new ApplicationObjectMapper(new ObjectMapper());
    }
}
