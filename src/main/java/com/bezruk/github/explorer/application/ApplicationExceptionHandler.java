package com.bezruk.github.explorer.application;

import com.bezruk.github.explorer.exception.ApplicationException;
import com.bezruk.github.explorer.model.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
@ComponentScan("com.bezruk.github.explorer.application")
@RequiredArgsConstructor
public class ApplicationExceptionHandler implements ErrorWebExceptionHandler {

    private final DataBufferWriter dataBufferWriter;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationError> handleApplicationException(ApplicationException e) {
        return ResponseEntity
                .status(e.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromApplicationException(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationError> handleException(Exception e) {

        return ResponseEntity
                .status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromException(e));
    }

    private ApplicationError fromApplicationException(ApplicationException exception) {
        return new ApplicationError(exception.getMessage(), exception.getStatus());
    }

    private ApplicationError fromException(Exception exception) {
        return new ApplicationError(exception.getMessage(), 500);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ApplicationError error = new ApplicationError(ex.getMessage(), 500);

        if (ex instanceof ApplicationException exception) {
            error = new ApplicationError(exception.getMessage(), exception.getStatus());
        }

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        exchange.getResponse().setStatusCode(HttpStatus.resolve(error.status()));
        return dataBufferWriter.write(exchange.getResponse(), error);
    }
}