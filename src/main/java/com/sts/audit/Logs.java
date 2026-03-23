package com.sts.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Logs {

    public List<String> formatLogs() {
        List<String> Logs = new ArrayList<>();

        String filePath = "data/audit_log.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String formatted = formatLog(parts[0], parts[1], parts[2], parts[3], parts[4]);
                Logs.add(formatted);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo logs", e);
        }

        for (String log : Logs) {
            System.out.println(log);
        }

        return Logs;
    }

    private String formatLog(String timestamp, String module, String action, String status, String details) {
        return String.format("[%s] %s | %s | %s -> %s", status, timestamp, module, action, details);
    }
}