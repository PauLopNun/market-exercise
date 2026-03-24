package com.sts.payment.data_access;

import lombok.Getter;
import lombok.Setter;

public class User {

    @Getter
    private String id, name;

    @Setter
    @Getter
    private double budget;

    public User(String id, String name, double budget){
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

}
