package com.sts.shared.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    public static User getUser(String id) throws UserNotFoundException {

        List<String[]> csvUserList = getData("./data/users.csv", ",");
        for(String[] userLine : csvUserList){
            //System.out.println(userLine[0] + " | " + id + " , " + userLine[0].equals(id));
            if(userLine[0].equals( id ) || userLine[1].equals( id )){

                String idUser = userLine[0];
                String nameUser = userLine[1];
                double budget_user = Double.parseDouble(userLine[2]);
                return new User( idUser, nameUser, budget_user);
            }
        }
        throw new UserNotFoundException(id);
        //return new User( "N/A", "N/A", 0.0);
    }

    public static List<String[]> getData(String csvFile, String delimiter) {
        List<String[]> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                result.add(values);
                //System.out.println(Arrays.toString(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
