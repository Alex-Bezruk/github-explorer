package com.bezruk.github.explorer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    private Long id;
    private String name;
    private Owner owner;
    private List<Branch> branches;
}
