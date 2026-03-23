package com.sts.shared.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    public String[][] getUser(int id) {
        return null;
    }
    public List<String[]> getData(String csvFile, String delimiter) {
        List<String[]> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                result.add(values);
                System.out.println(Arrays.toString(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
