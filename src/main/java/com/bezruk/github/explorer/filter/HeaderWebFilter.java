package com.bezruk.github.explorer.filter;

import com.bezruk.github.explorer.exception.UnsupportedMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class HeaderWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        List<MediaType> mediaTypes = exchange.getRequest().getHeaders().getAccept();
        if (mediaTypes.contains(MediaType.APPLICATION_XML)) {
            String message = String.format("Media type %s is not supported by this API.", MediaType.APPLICATION_XML_VALUE);
            throw new UnsupportedMediaTypeException(message);
        }

        return chain.filter(exchange);
    }
}