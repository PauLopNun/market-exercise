package com.sts.client;

import com.sts.shared.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final String filePath;

    public UserRepository(String filePath) {
        this.filePath = filePath;
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] csvFields = line.split(",");
                users.add(new User(csvFields[0], csvFields[1], Double.parseDouble(csvFields[2])));
            }
        }
        return users;
    }

    public User findByIdOrName(String input) throws IOException {
        for (User user : findAll()) {
            if (user.getId().equals(input) || user.getName().equalsIgnoreCase(input)) {
                return user;
            }
        }
        return null;
    }
}
