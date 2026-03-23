package com.sts.client.service;
import com.sts.shared.model.CSVReader;
import com.sts.shared.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserLogin {

    public static User login(String identifier){
        return CSVReader.getUser(identifier);
    }
}
