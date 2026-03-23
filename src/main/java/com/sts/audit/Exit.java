package com.sts.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Exit {

    public static void main(String[] args) {
        String filePath = "data/audit_log.csv";
        List<String> details = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 0) continue;

                details.add(parts[parts.length - 1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(details);
    }
}