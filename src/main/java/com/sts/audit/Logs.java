package com.sts.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Logs {

    private String filePath;

    public Logs(String filePath) {
        this.filePath = filePath;
    }

    public List<String> formatLogs() {
        List<String> logs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 5);
                if (parts.length < 5) continue;

                String formatted = formatLog(parts[0], parts[1], parts[2], parts[3], parts[4]);
                logs.add(formatted);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo logs", e);
        }

        return logs;
    }

    private String formatLog(String timestamp, String module, String action, String status, String details) {
        return String.format("[%s] %s | %s -> %s",
                status, timestamp, action, details.replace("\"", ""));
    }
}