package com.sts.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private double budget;


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

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


