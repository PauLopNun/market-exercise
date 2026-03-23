package com.sts.shared.model;

public class User {
    private String id;
    private String name;
    private double budget;

    public User(String id, String name, double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return  false;

        User user = (User) o;
        return this.id.equals(user.id);

    }

    //Ni idea
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


