package com.bezruk.github.explorer.exception;

public class GithubRequestException extends ApplicationException {
    public GithubRequestException(Integer status, String url) {
        super(status, String.format("GithubRequest failed. Request url: %s", url));
    }
}
