package com.sts.payment;

public class User {
    private String id;
    private String name;
    private double budget;

    public User(String id, String name, double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getBudget() { return budget; }

    public void setBudget(double budget) { this.budget = budget; }
}
