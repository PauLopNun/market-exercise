package com.sts.client.service;
import com.sts.shared.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserLogin {

    ArrayList<User> users;

    public UserLogin(ArrayList<User> users) {
        this.users = users;
    }

    public User login(String identifier){
        for(User user:users){
            if(user.getName().equals(identifier) || user.getId().equals(identifier)){
                return user;
            }
        }
        throw new IllegalArgumentException("USER NOT FOUND");
    }

}
