package com.sts.shared.model;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("USER BY NAME/ID: " + userId + " NOT FOUND");
    }
}
