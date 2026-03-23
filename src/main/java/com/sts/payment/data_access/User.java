package com.sts.payment.data_access;

public class User {

    private String id, name;
    private double budget;

    public User(String id, String name, double budget){
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }
}
