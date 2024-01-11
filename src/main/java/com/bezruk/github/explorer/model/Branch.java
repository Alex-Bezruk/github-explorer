package com.bezruk.github.explorer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Branch(String name, Commit commit) { }