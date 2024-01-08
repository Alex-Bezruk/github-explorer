package com.bezruk.github.explorer.model;

import lombok.Data;

@Data
public class Branch {
    private String name;
    private Commit commit;
}