package com.sts.audit;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditorWriterCsv {


    private final String filePath;

    public AuditorWriterCsv(String filePath) {
        this.filePath = filePath;
    }

    public void log(String module, EventType action, String status, String details) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            String timestamp = LocalDateTime.now().toString();

            out.printf("%s,%s,%s,%s,%s%n", timestamp, module, action.name(), status, details);
        } catch (Exception e) {
            System.err.println("Error al escribir log: " + e.getMessage());
        }
    }
}