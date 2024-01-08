package com.bezruk.github.explorer.model;

import lombok.Data;

@Data
public class Repository {
    private Long id;
    private String name;
    private Owner owner;
}