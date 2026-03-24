package com.sts.shared.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String id;
    private String name;
    private double budget;

    public User(String id, String name, double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
    }
}
